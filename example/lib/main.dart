import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:hwm/hwm.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await Hwm.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        body: Stack(
          children: <Widget>[
            Positioned(
              top: 20,
              left: 20,
              width: 100,
              height: 80,
              child: MaterialButton(
                height: 100,
                color: Color.fromARGB(255, 98, 123, 255),
                onPressed: (){
                  Hwm.hwminit("");
                },

                child: Text("注册 （必须）"),
              ),
            ),
            Positioned(
              top: 120,
              left: 20,
              width: 100,
              height: 80,
              child: MaterialButton(
                height: 100,
                color: Color.fromARGB(255, 98, 123, 255),
                onPressed: (){
                  Hwm.hwmlogin("");
                },

                child: Text("登陆"),
              ),
            ),
            Positioned(
              top: 220,
              left: 20,
              width: 100,
              height: 80,
              child: MaterialButton(
                height: 100,
                color: Color.fromARGB(255, 98, 123, 255),
                onPressed: (){
                  Hwm.hwmlogout("");
                },

                child: Text("登出"),
              ),
            ),
            Positioned(
              top: 320,
              left: 20,
              width: 100,
              height: 80,
              child: MaterialButton(
                height: 100,
                color: Color.fromARGB(255, 98, 123, 255),
                onPressed: (){
                  Hwm.hwmcall("");
                },

                child: Text("呼叫"),
              ),
            ),
            Positioned(
              top: 420,
              left: 20,
              width: 100,
              height: 80,
              child: MaterialButton(
                height: 100,
                color: Color.fromARGB(255, 98, 123, 255),
                onPressed: (){
                  Hwm.hwmaddmeeting("");
                },

                child: Text("创建会议"),
              ),
            ),
            Positioned(
              top: 520,
              left: 20,
              width: 100,
              height: 80,
              child: MaterialButton(
                height: 100,
                color: Color.fromARGB(255, 98, 123, 255),
                onPressed: (){
                  Hwm.hwmjoinmeeting("");
                },

                child: Text("加入会议"),
              ),
            ),
          ],
        ),
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        // body: Center(
        //   child: Text('Running on: $_platformVersion\n'),
        // ),
      ),
    );
  }
}
