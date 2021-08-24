
import 'dart:async';

import 'package:flutter/services.dart';

class Hwm {
  static const MethodChannel _channel =
  const MethodChannel('hwm');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static void get hwminit async {
    _channel.invokeMethod('hwm', {
      "callType": "5"
    });
  }
  static void get hwmlogin async {
    _channel.invokeMethod('hwm', {
      "callType": "3",
      "userName": "夏雷",
      "userId": "18701337589",
    });
  }

  static void get hwmlogout async {
    _channel.invokeMethod('hwm', {
      "callType": "4",
    });
  }

  static void get hwmcall async {
    _channel.invokeMethod('hwm', {
      "callType": "0",
      "meetingTitle": "hhh",
      "isVideo": "1",
      "callList": ""
    });
  }

  static void get hwmaddmeeting async {
    _channel.invokeMethod('hwm', {
      "callType": "1",
      "meetingTitle": "hhh",
      "isVideo": "1",
      "needPassword": "0",
      "callList": ""
    });
  }

  static void get hwmjoinmeeting async {
    _channel.invokeMethod('hwm', {
      "callType": "2",
      "meetingId": "983368893",
      "password": "",
    });
  }
}