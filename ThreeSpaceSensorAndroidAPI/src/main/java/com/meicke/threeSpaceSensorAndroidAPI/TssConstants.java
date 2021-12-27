package com.meicke.threeSpaceSensorAndroidAPI;

import java.util.UUID;

/**
 * Final class containing all constants and literals of the 3Space Sensor API.
 */
public final class TssConstants {

    /** UUID of the API. Needed for establishing a connection to the sensor! */
    public static final UUID TssUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    /** Name of the Bluetooth Mini Sensor. Needed for pairing with the sensor via Bluetooth! */
    public static final String TssName = "YostLabsMBT";

    /* Some relevant command bytes, that the sensor can process according to its manual. */
    public static final Byte TssCommand_GetBaudRate                     = (byte) 0xe8;
    public static final Byte TssCommand_GetFilterUpdateRate             = (byte) 0x84;
    public static final Byte TssCommand_GetFirmwareVersion              = (byte) 0xdf;
    public static final Byte TssCommand_GetHardwareVersion              = (byte) 0xe6;
    public static final Byte TssCommand_GetOversampleRate               = (byte) 0x90;
    public static final Byte TssCommand_GetTaredOrientationAsQuaternion = (byte) 0x00;

    public static final Byte TssCommand_SetBaudRate                     = (byte) 0xe7;
    public static final Byte TssCommand_SetFilterUpdateRate             = (byte) 0x67;
    public static final Byte TssCommand_SetOversampleRate               = (byte) 0x6a;

    public static final Byte TssCommand_SetStreamingSlots               = (byte) 0x50;
    public static final Byte TssCommand_SetStreamingTiming              = (byte) 0x52;
    public static final Byte TssCommand_SetTareCurrentOrientation       = (byte) 0x60;
    public static final Byte TssCommand_SetTareQuaternion               = (byte) 0x61;
    public static final Byte TssCommand_SetWiredResponseHeaderBit       = (byte) 0xdd;

    public static final Byte TssCommand_StartStream                     = (byte) 0x55;
    public static final Byte TssCommand_StopStream                      = (byte) 0x56;

    /* Default streaming parameter for the sensor API */
    public static final int TssStream_DefaultInterval = 1000;
    public static final int TssStream_DefaultDuration = 0xffffffff;
    public static final int TssStream_DefaultDelay    = 0;

    /** Allowed values for the Baud rate settings of the sensor. */
    public static final int [] TssBaudRates = { 1200, 2400, 4800, 9600, 19200, 28800, 38400, 57600, 115200, 230400, 460800, 921600 };

    /* Error messages */ //TODO: Store these at a more appropriate location!!!
    public static final String TssError_SensorNotPaired              = "Der Sensor ist nicht mit dem Gerät gekoppelt. Bitte führen Sie zunächst die Kopplung manuell durch und versuchen Sie es anschließend erneut.";
    public static final String TssError_SocketConnectFailed          = "Verbindung zum Sensor via Bluetooth-Socket konnte nicht hergestellt werden.";
    public static final String TssError_SocketDisconnectFailed       = "Trennen der Verbindung zum Sensor fehlgeschlagen.";
    public static final String TssError_SocketStreamUnavailable      = "Zugriff auf den Input-/Output-Stream des Bluetooth-Sockets fehlgeschlagen";
    public static final String TssError_StopStreamByteSkipFailed     = "Leeren des Input-Streams nach Stop-Anfrage des Datenstroms fehlgeschlagen.";
    public static final String TssError_OutputBufferWriteFailed      = "Fehler beim Schreiben von Daten in den Output-Stream des Bluetooth-Sockets.";
    public static final String TssError_InputBufferReadFailed        = "Fehler beim Lesen von Daten vom Input-Stream des Bluetooth-Sockets.";
    public static final String TssError_InputBufferReadTimeout       = "Zeitüberschreitung beim Lesen des Input-Streams des Bluetooth-Sockets.";
    public static final String TssError_InputBufferAvailableError    = "Fehler bei der Abfrage der Anzahl lesbarer Bytes im Input-Stream.";
    public static final String TssError_IllegalBaudRateValue         = "Der angegebene Wert wird vom Sensor als Baud-Rate nicht unterstützt.";
    public static final String TssError_IllegalOversampleRateValue   = "Der angegebene Wert wird vom Sensor als Oversample-Rate nicht unterstützt";
    public static final String TssError_IllegalFilterUpdateRateValue = "Der angegebene Wert wird vom Sensor als Filteraktualisierungsrate nicht unterstützt.";
    public static final String TssError_ConnectionTimeout            = "Zeitüberschreitung beim Herstellen der Verbindung.";
}
