
import 'dart:async';

import 'package:flutter/services.dart';

class Hwm {
  static const MethodChannel _channel =
  const MethodChannel('hwm');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static void hwminit(String json) async {
    _channel.invokeMethod('hwminit',json);
  }
  static void hwmlogin(String json) async {
    _channel.invokeMethod('hwmlogin',json);
  }

  static void hwmlogout(String json) async {
    _channel.invokeMethod('hwmlogout', json);
  }

  static void hwmcall(String json) async {
    _channel.invokeMethod('hwmcall', json);
  }

  static void hwmaddmeeting(String json) async {
    _channel.invokeMethod('hwmaddmeeting', json);
  }

  static void hwmjoinmeeting(String json) async {
    _channel.invokeMethod('hwmjoinmeeting', json);
  }
  static void hwmordermeeting(String json) async {
    _channel.invokeMethod('hwmordermeeting', json);
  }
}