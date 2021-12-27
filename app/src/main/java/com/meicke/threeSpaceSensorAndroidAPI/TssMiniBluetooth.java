package com.meicke.threeSpaceSensorAndroidAPI;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import static com.meicke.threeSpaceSensorAndroidAPI.TssConstants.*;

import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssCommunicationException;
import com.meicke.threeSpaceSensorAndroidAPI.Exceptions.TssConnectionException;

/**
 * API to connect and communicate with YostLabs 3-Space Sensor models.
 * The pairing of the sensor with an android device has to be done manually before using this code!
 */
public class TssMiniBluetooth {

    // Bluetooth related variables
    private BluetoothSocket btSocket;
    private OutputStream btOutputStream;
    private InputStream btInputStream;
    private UUID uuid;

    // Communication and streaming related variables
    private boolean isStreaming;
    private ReentrantLock commLock;
    private float[] lastPacket = new float[]{0,0,0,1};
    private Vector<Byte> streamData = new Vector<>();
    private final String address;

    /**
     * Class constructor. This is a singleton class. Use getInstance method instead!
     * @throws TssConnectionException Error based on the bluetooth connection with the sensor.
     */
    public TssMiniBluetooth(String address, boolean autoConnect) throws TssConnectionException {
        this.address = address;
        if (autoConnect) connectSocket();
    }

    /**
     * Returns the class instance of ThreeSpaceMiniBluetooth and automatically connects the sensor
     * in case of a new instance.
     * @throws TssConnectionException Error based on the bluetooth connection with the sensor.
     */
/*    public static TssMiniBluetooth getInstance () throws TssConnectionException {
        if (instance == null) instance = new TssMiniBluetooth(true);
        return instance;
    }

    /**
     * Returns the class instance of ThreeSpaceMiniBluetooth.
     * @param autoConnect Controls if the socket connection should be established automatically.
     * @throws TssConnectionException Error based on the bluetooth connection with the sensor.
     */
/*    public static TssMiniBluetooth getInstance (boolean autoConnect) throws TssConnectionException {
        if (instance == null) instance = new TssMiniBluetooth(autoConnect);
        return instance;
    }
*/
    /**
     * Returns true, if this singleton class already has an instance.
     * @return True, if TssMiniBluetooth instance exists.
     */
/*     public static boolean hasInstance () {
        return instance != null;
    }

    /**
     * Returns true if the communication socket is connected
     * @return True if the bluetooth socket is connected to the sensor.
     */
    public boolean getIsConnected() {
        return (btSocket != null && btSocket.isConnected());
    }

    /**
     * Returns the flag indicating if the data stream is active.
     * Does not explicitly check this information with the sensor!
     * @return True if the sensor is currently streaming data.
     */
    public boolean getIsStreaming() {
        return isStreaming;
    }

    /**
     * Calculates a single byte checksum for the input data by adding up all bytes.
     * Technically this is questionable, but the sensor requires this checksum.
     * @param data Byte array input.
     */
    private byte createChecksum ( byte[] data ) {
        byte checkSum = 0;
        for ( byte value : data ) checkSum += value % 256;
        return checkSum;
    }

    /**
     * Writes the byte array 'data' into the output stream of the communication socket.
     * Do not use this method directly! Use requestAndRead instead!
     * @param data Byte array that gets put into the buffer.
     * @throws TssCommunicationException Writing bytes to output buffer failed.
     */
    private void writeToBuffer ( byte[] data ) throws TssCommunicationException {
        writeToBuffer( data, false );
    }

    /**
     * Writes the byte array 'data' into the output stream of the communication socket.
     * Do not use this method directly! Use requestAndRead instead!
     * @param data Byte array that gets put into the buffer.
     * @throws TssCommunicationException Writing bytes to output buffer failed.
     */
    private void writeToBuffer (byte[] data, boolean returnHeader) throws TssCommunicationException {
        byte[] buffer = new byte[data.length + 2];
        System.arraycopy( data, 0, buffer, 1, data.length );
        buffer[0] = returnHeader ? (byte) 0xf9 : (byte) 0xf7;
        buffer[data.length + 1] = createChecksum( data );

        try {
            btOutputStream.write( buffer );
            btOutputStream.flush();
        } catch ( IOException e ) {
            throw new TssCommunicationException( TssError_OutputBufferWriteFailed );
        }
    }

    /**
     * Reads the requested amount of bytes from the connection sockets input stream.
     * This method times out after 5 seconds, to prevent endless loops when reading
     * from the buffer. |Do not use this method directly! Use requestAndRead instead!|
     * @param numberOfBytes Number of bytes that will be read from the input stream.
     * @throws TssCommunicationException Reading bytes from input stream failed.
     */
    private byte[] readFromBuffer (int numberOfBytes) throws TssCommunicationException{
        Instant timeOutLimit = Instant.now().plusSeconds(5);
        byte[] data = new byte[numberOfBytes];
        int bytesRead = 0;

        while (bytesRead < numberOfBytes) {
            try {
                bytesRead += btInputStream.read(data, bytesRead, numberOfBytes - bytesRead);
            } catch (IOException e) {
                throw new TssCommunicationException(TssError_InputBufferReadFailed);
            }

            if (Instant.now().isAfter(timeOutLimit)) {
                throw new TssCommunicationException(TssError_InputBufferReadTimeout);
            }
        }

        return data;
    }

    /**
     * Starts the connection to the sensor and creates a new bluetooth socket instance.
     * @throws TssConnectionException Error based on the bluetooth connection with the sensor.
     */
    public void connectSocket() throws TssConnectionException {

        if (btSocket != null && btSocket.isConnected()) return;

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice bluetoothDevice = null;

        // Connect sensor using the list of previously paired bluetooth devices
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices){
            if (device.getAddress().contains(address)){
                bluetoothDevice = device;
                break;
            }
        }

        // If the sensor hasn't been paired before, throw a new connection exception.
        // (Sensor has to be paired manually beforehand. Implementing a pairing dialog
        // does not belong in this API, and has to be implemented in the activity that
        // calls the API instead! Please refer to the Android Bluetooth Guidelines for more info.
        if (bluetoothDevice == null) {
            throw new TssConnectionException(TssError_SensorNotPaired);
        }

        // Create connection via Bluetooth Socket, then prepare I/O streams and the communication lock
        try {
            btSocket = bluetoothDevice.createRfcommSocketToServiceRecord(TssUuid);
            btSocket.connect();
        } catch (IOException e) {
            throw new TssConnectionException(TssError_SocketConnectFailed);
        }

        try {
            btInputStream = btSocket.getInputStream();
            btOutputStream = btSocket.getOutputStream();
        } catch (IOException e) {
            throw new TssConnectionException(TssError_SocketStreamUnavailable);
        }

        // Never forget this lock! It's essential to ensuring serialization of the sensor communication.
        commLock = new ReentrantLock();
    }

    /**
     * Closes the connection to the sensor.
     * @throws TssConnectionException Error while closing the Bluetooth socket.
     */
    public void disconnectSocket() throws TssConnectionException {
        if (btSocket.isConnected()) {
            try {
                btSocket.close();
            } catch (IOException e) {
                throw new TssConnectionException(TssError_SocketDisconnectFailed);
            }
        }
    }

    /**
     * Returns the firmware version of the bluetooth sensor.
     * @return Firmware version of the sensor.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public String getFirmwareVersion() throws TssCommunicationException {
        return new String(sendAndReceive(TssCommand_GetFirmwareVersion, 12));
    }

    /**
     * Returns the hardware version of the bluetooth sensor.
     * @return Hardware version of the sensor.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public String getHardwareVersion() throws TssCommunicationException {
        return new String(sendAndReceive(TssCommand_GetHardwareVersion, 32));
    }

    /**
     * Returns the baud rate of the sensor.
     * @return Baud rate of the sensor.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public int getBaudRate() throws TssCommunicationException {
        return ByteBuffer.wrap(sendAndReceive(TssCommand_GetBaudRate, 4)).getInt();
    }

    /**
     * Sets a new baud rate value for the sensor. (Value persists until the sensor is reset or
     * the values are explicitly committed.)
     * @param baudRate New baud rate.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public void setBaudRate(int baudRate) throws TssCommunicationException {
        if ( Arrays.stream( TssBaudRates ).anyMatch( n -> n == baudRate ) ){
            send( TssCommand_SetBaudRate, ByteBuffer.allocate(4).putInt(baudRate).array() );
        } else {
            throw new TssCommunicationException( TssError_IllegalBaudRateValue );
        }
    }

    /**
     * Returns the filter update rate in microseconds.
     * @return Filter update rate of the sensor.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public int getFilterUpdateRate() throws TssCommunicationException {
        return sendAndReceive(TssCommand_GetFilterUpdateRate, 4)[0];
    }

    /**
     * Sets a new filter update rate for the sensor. (Value persists until the sensor is reset or
     * the values are explicitly committed.)
     * @param filterUpdateRate New filter update rate.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public void setFilterUpdateRate(int filterUpdateRate) throws TssCommunicationException {
        if ( filterUpdateRate > 0 && filterUpdateRate <= 100000 ) {
            send( TssCommand_SetFilterUpdateRate, ByteBuffer.allocate(4).putInt(filterUpdateRate).array() );
        } else {
            throw new TssCommunicationException( TssError_IllegalFilterUpdateRateValue );
        }
    }

    /**
     * Fetches the current oversampling rate from the sensor and returns it as an integer value.
     * @return Current oversampling rate.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public int getOversampleRate () throws TssCommunicationException {
        return sendAndReceive(TssCommand_GetOversampleRate, 1)[0];
    }

    /**
     * Sets a new value for the oversampling rate of the sensor.
     * @param oversampleRate New oversampling rate.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public void setOversampleRate (int oversampleRate) throws TssCommunicationException {
        if ( oversampleRate > 0 && oversampleRate <= 10 ) {
            send( TssCommand_SetOversampleRate, new byte[] { (byte) oversampleRate } );
        } else {
            throw new TssCommunicationException( TssError_IllegalOversampleRateValue );
        }
    }

    /**
     * Tares the sensor, using the current orientation.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public void setCurrentOrientationAsTare () throws TssCommunicationException {
        send( TssCommand_SetTareCurrentOrientation, new byte[] {} );
    }

    /**
     * Tares the sensor, using a quaternion.
     * @param q Quaternion which is supposed to be the new tare position.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public void setQuaternionAsTare(Quaternion q ) throws TssCommunicationException {
        send( TssCommand_SetTareQuaternion, q.toBytes() );
    }

    /**
     * Writes a command and some payload data to the output buffer.
     * @param commandByte The command encoded in a single byte.
     * @param data Payload data to be send with the command.
     * @throws TssCommunicationException Error during sensor communication.
     */
    private void send (byte commandByte, byte [] data) throws TssCommunicationException {
        boolean reenableStream = false;

        if (isStreaming) {
            stopStream();
            reenableStream = true;
        }

        commLock.lock();
        byte[] buffer = new byte[data.length + 1];
        System.arraycopy( data, 0, buffer, 1, data.length );
        buffer[0] = commandByte;
        writeToBuffer(buffer);
        commLock.unlock();

        if (reenableStream) startStream();
    }

    /**
     * Writes a command to the output buffer and returns the uninterpreted response as an byte array.
     * @param commandByte The command encoded in a single byte.
     * @param responseLength The number of expected bytes for the sensors response.
     * @return The uninterpreted sensor response as a byte array.
     * @throws TssCommunicationException Error during sensor communication.
     */
    private byte[] sendAndReceive (byte commandByte, int responseLength) throws TssCommunicationException {
        boolean reenableStream = false;

        if (isStreaming) {
            stopStream();
            reenableStream = true;
        }

        commLock.lock();
        writeToBuffer( new byte[] { commandByte } );
        byte[] response = readFromBuffer(responseLength);
        commLock.unlock();

        if (reenableStream) startStream();
        return response;
    }

    /**
     * Starts the data stream of the sensor. (Enables reading current orientation/rotation from
     * stream as quaternions. -> 'getFilteredTaredOrientationQuaternions')
     * @throws TssCommunicationException Error during sensor communication.
     */
    public void startStream() throws TssCommunicationException {

        commLock.lock();
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(0x48);
        byte[] header = buffer.array();

        writeToBuffer( new byte[] { TssCommand_SetWiredResponseHeaderBit,
                header[0], header[1], header[2], header[3]});

        writeToBuffer( new byte[] { TssCommand_SetStreamingSlots,
                (byte)   0, (byte) 255, (byte) 255, (byte) 255,
                (byte) 255, (byte) 255, (byte) 255, (byte) 255 } );

        buffer.putInt(0, TssStream_DefaultInterval);
        byte[] interval = buffer.array();
        buffer.putInt(0, TssStream_DefaultDuration);
        byte[] duration = buffer.array();
        buffer.putInt(0, TssStream_DefaultDelay);
        byte[] delay    = buffer.array();

        writeToBuffer( new byte[] { TssCommand_SetStreamingTiming,
                interval[0], interval[1], interval[2], interval[3],
                duration[0], duration[1], duration[2], duration[3],
                delay[0],    delay[1],    delay[2],    delay[3] } );

        writeToBuffer( new byte[] { TssCommand_StartStream }, true );
        commLock.unlock();
        isStreaming = true;
    }

    /**
     * Stops the data stream of the sensor and waits for the input stream to empty completely.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public void stopStream () throws TssCommunicationException {
        //TODO: Maybe waiting for the input buffer to empty isn't always needed, e.g. for a simple disconnect?
        if (!getIsStreaming()) return;

        commLock.lock();
        writeToBuffer( new byte[] { TssCommand_StopStream } );

        try{
            while ( btInputStream.available() != 0 ) {
                btInputStream.skip( btInputStream.available() );
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e ) {
            throw new TssCommunicationException( TssError_StopStreamByteSkipFailed );
        } finally {
            commLock.unlock();
            isStreaming = false;
        }
    }

    /**
     * Returns the current orientation of the sensor as quaternion.
     * @return Orientation Quaternion
     * @throws TssCommunicationException Error during sensor communication.
     */
    public Quaternion getOrientationAsQuaternion() throws TssCommunicationException {
        float [] rawValue = getOrientationAsQuaternions();
        return new Quaternion(rawValue[3], rawValue[0], rawValue[1], rawValue[2]);
    }

    /**
     * Returns the current orientation of the sensor as quaternions in the form (x, y, z, w).
     * Reads data from stream, if sensor is streaming, otherwise it sends a single request
     * for the data. The method returns the measurements filtered and tared.
     * @return Quaternions as an array of four float values.
     * @throws TssCommunicationException Error during sensor communication.
     */
    public float[] getOrientationAsQuaternions() throws TssCommunicationException {

        byte[] response;

        if ( !isStreaming ) {
            response = sendAndReceive( TssCommand_GetTaredOrientationAsQuaternion, 16 );
            float x = ByteBuffer.wrap( response, 0, 4 ).getFloat();
            float y = ByteBuffer.wrap( response, 4, 4 ).getFloat();
            float z = ByteBuffer.wrap( response, 8, 4 ).getFloat();
            float w = ByteBuffer.wrap( response,12, 4 ).getFloat();
            return new float[] {x, y, z, w};
        } else {

            commLock.lock();

            try {
                if ( streamData.size() + btInputStream.available() < 18 ) return lastPacket;
                response = readFromBuffer( btInputStream.available() );
            } catch ( IOException e ) {
                throw new TssCommunicationException( TssError_InputBufferAvailableError );
            } finally {
                commLock.unlock();
            }

            for ( byte value : response ) streamData.add( value );

            int location = streamData.size() - 18;
            while ( location > 0 ) {

                byte checksum = (byte) streamData.toArray()[location];
                byte dataLength = (byte) streamData.toArray()[location + 1];

                if ( (dataLength & 255) == 16 ) {
                    byte result = 0;
                    byte[] quaternion = new byte[16];

                    for ( int i = 0; i < 16; i++ ) {
                        quaternion[i] = (byte) streamData.toArray()[location + 2 + i];
                        result = (byte) ( result + quaternion[i] );
                    }

                    if ( (result & 255) == (checksum & 255) ) {
                        float[] packet = new float[4];
                        ByteBuffer.wrap( quaternion ).asFloatBuffer().get( packet );
                        if ( quaternionCheck( packet ) ){
                            streamData.subList( 0, location + 18 ).clear();
                            lastPacket = packet;
                            return lastPacket;
                        }
                    }
                }

                location--;
            }

            return lastPacket;
        }
    }

    /**
     * Checks if the provided array contains correctly constructed quaternions
     * @param quaternion Array with four float values that might be quaternions.
     * @return True if the provided values are mathematically correct quaternions.
     */
    private boolean quaternionCheck ( float[] quaternion ) {
        if ( quaternion.length != 4 ) return false;

        double length = Math.sqrt( quaternion[0] * quaternion[0] + quaternion[1] * quaternion[1] +
                                   quaternion[2] * quaternion[2] + quaternion[3] * quaternion[3] );

        return Math.abs( 1 - length ) < 1f;
    }

}