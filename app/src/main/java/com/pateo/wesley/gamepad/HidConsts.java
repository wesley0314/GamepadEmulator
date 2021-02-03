package com.pateo.wesley.gamepad;

import android.bluetooth.BluetoothHidDevice;

public class HidConsts {
    public static BluetoothHidDevice HidDevice;

    public static final String NAME = "PATEO-GAMEPAD";
    public static final String DESCRIPTION = "Play games via your phone";
    public static final String PROVIDER = "PATEO";
    public static final byte[] DESCRIPTOR = new byte[]{

            // HID descriptor
            0x09, // bLength
            0x21, // bDescriptorType - HID
            0x11, 0x01, // bcdHID (little endian - 1.11)
            0x00, // bCountryCode
            0x01, // bNumDescriptors (min 1)
            0x22, // bDescriptorType - Report
            0x30, 0x00, // wDescriptorLength (48)

            // Report descriptor
            0x05, 0x01,        // USAGE_PAGE (Generic Desktop)
            0x09, 0x05,        // USAGE (Game Pad)
            (byte) 0xa1, 0x01, // COLLECTION (Application)
            (byte) 0xa1, 0x00, //   COLLECTION (Physical)
            0x05, 0x09,        //     USAGE_PAGE (Button)
            0x19, 0x01,        //     USAGE_MINIMUM (Button 1)
            0x29, 0x04,        //     USAGE_MAXIMUM (Button 4)
            0x15, 0x00,        //     LOGICAL_MINIMUM (0)
            0x25, 0x01,        //     LOGICAL_MAXIMUM (1)
            0x75, 0x01,        //     REPORT_SIZE (1)
            (byte) 0x95, 0x04, //     REPORT_COUNT (4)
            (byte) 0x81, 0x02, //     INPUT (Data,Var,Abs)
            0x75, 0x04,        //     REPORT_SIZE (4)
            (byte) 0x95, 0x01, //     REPORT_COUNT (1)
            (byte) 0x81, 0x03, //     INPUT (Cnst,Var,Abs)
            0x05, 0x01,        //     USAGE_PAGE (Generic Desktop)
            0x09, 0x30,        //     USAGE (X)
            0x09, 0x31,        //     USAGE (Y)
            0x15, (byte) 0x81, //     LOGICAL_MINIMUM (-127)
            0x25, 0x7f,        //     LOGICAL_MAXIMUM (127)
            0x75, 0x08,        //     REPORT_SIZE (8)
            (byte) 0x95, 0x02, //     REPORT_COUNT (2)
            (byte) 0x81, 0x02, //     INPUT (Data,Var,Abs)
            (byte) 0xc0,       //   END_COLLECTION
            (byte) 0xc0        // END_COLLECTION

    };
}
