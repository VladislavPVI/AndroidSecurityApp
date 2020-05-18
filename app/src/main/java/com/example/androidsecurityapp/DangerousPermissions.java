package com.example.androidsecurityapp;

import java.util.EnumSet;
import java.util.HashSet;

public enum DangerousPermissions {
    READ_CALENDAR, WRITE_CALENDAR, CAMERA, READ_CONTACTS, WRITE_CONTACTS, GET_ACCOUNTS,
    ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, RECORD_AUDIO, READ_PHONE_STATE,
    READ_PHONE_NUMBERS, CALL_PHONE, ANSWER_PHONE_CALLS, READ_CALL_LOG, WRITE_CALL_LOG,
    ADD, VOICEMAIL, USE_SIP, PROCESS_OUTGOING_CALLS,
    BODY_SENSORS, SEND_SMS, RECEIVE_SMS, READ_SMS, RECEIVE_WAP_PUSH, RECEIVE_MMS,
    READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, CALL_LOG;

    private static final HashSet<String> nameToSet =
            new HashSet<String>();

    static {
        for (DangerousPermissions value : EnumSet.allOf(DangerousPermissions.class)) {
            nameToSet.add("android.permission." + value.name());
        }
    }

    public static Boolean forCont(String name) {
        return nameToSet.contains(name);
    }
}
