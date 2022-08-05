
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'wearable_communicator_platform_interface.dart';

class WearableCommunicator {
  Future<String?> getPlatformVersion() {
    return WearableCommunicatorPlatform.instance.getPlatformVersion();
  }

  static const MethodChannel _channel =
  MethodChannel('wearableCommunicator');

  /// send message to watch
  static void sendMessage({String? nodeID, Map<String, dynamic>? data}) async {
    await _channel.invokeMethod('sendMessage', {
      "node_id": nodeID,
      "data": data
    });
  }

  /// get all paired connected nodes
  static getAllConnectedNodes() async{
    final result = await _channel.invokeMethod('getWearableNode');
    return result;
  }

  /// get all paired nodes with app isntalled
  static getAllNodesWithInstalledApp() async{
    final result = await _channel.invokeMethod('getWearableNodeWithInstallApp');
    return result;
  }

  /// get all paired nodes with app isntalled
  static getAllConnectedAndInstalledApp() async{
    final result = await _channel.invokeMethod('getAllConnectedAndInstalledNodes');
    return result;
  }

  /// open play store in paired node that does not has installed app
  static openPlayStoreInWearable({String? nodeId, String? marketId}) async{
    final result = await _channel.invokeMethod('openPlayStoreInWearable',
        {"node_id":"$nodeId", "market_id": "$marketId"});
    return result;
  }
}

/// typedef for listener callbacks
typedef MultiUseCallback = void Function(dynamic msg);

/// Holder for wearable data and messages
class WearableListener {
  /// instances
  static const _channel = MethodChannel("wearableCommunicator");

  /// variables
  static int _nextCallbackId = 0;
  static final Map<int, MultiUseCallback> _messageCallbacksById = {};
  static final Map<int, MultiUseCallback> _nodeCallbacksById = {};
  static final Map<int, MultiUseCallback> _nodeInstalledAppCallbacksById = {};
  static final Map<int, MultiUseCallback> _pairedDevicesById = {};
  static final Map<int, MultiUseCallback> _availableNodesById = {};


  /// initialize method channel
  WearableListener() {
    _channel.setMethodCallHandler(_methodCallHandler);
  }

  /// Method channel functions
  static Future<void> _methodCallHandler(MethodCall call) async {
    switch (call.method) {

      case 'messageReceived':
        if (call.arguments["args"] is String) {
          try {
            Map? value = json.decode(call.arguments["args"]);
            _messageCallbacksById[call.arguments["id"]]!(value);
          } catch (e) {
            _messageCallbacksById[call.arguments["id"]]!(
                call.arguments["args"]);
          }
        } else {
          _messageCallbacksById[call.arguments["id"]]!(call.arguments["args"]);
        }
        break;

      case 'availableNode':
        if (call.arguments["args"] is String) {
          try {
            Map? value = json.decode(call.arguments["args"]);
            _availableNodesById[call.arguments["id"]]!(value);
          } catch (e) {
            _availableNodesById[call.arguments["id"]]!(
                call.arguments["args"]);
          }
        } else {
          _availableNodesById[call.arguments["id"]]!(call.arguments["args"]);
        }
        break;

      case 'deviceReceived':
        if (call.arguments["args"] is String) {
          try {
            Map? value = json.decode(call.arguments["args"]);
            _pairedDevicesById[call.arguments["id"]]!(value);
          } catch (e) {
            _pairedDevicesById[call.arguments["id"]]!(
                call.arguments["args"]);
          }
        } else {
          _pairedDevicesById[call.arguments["id"]]!(call.arguments["args"]);
        }
        break;

      case 'getWearableNode':
        if (call.arguments["args"] is String) {
          try {
            Map? value = json.decode(call.arguments["args"]);
            _nodeCallbacksById[call.arguments["id"]]!(value);
          } catch (e) {
            _nodeCallbacksById[call.arguments["id"]]!(call.arguments["args"]);
          }
        } else {
          _nodeCallbacksById[call.arguments["id"]]!(call.arguments["args"]);
        }
        break;

      case 'getWearableNodeWithInstallApp':
        if (call.arguments["args"] is String) {
          try {
            Map? value = json.decode(call.arguments["args"]);
            _nodeInstalledAppCallbacksById[call.arguments["id"]]!(value);
          } catch (e) {
            _nodeInstalledAppCallbacksById[call.arguments["id"]]!(call.arguments["args"]);
          }
        } else {
          _nodeInstalledAppCallbacksById[call.arguments["id"]]!(call.arguments["args"]);
        }
        break;

      case 'getAllConnectedAndInstalledNodes':
        if (call.arguments["args"] is String) {
          try {
            Map? value = json.decode(call.arguments["args"]);
            _nodeInstalledAppCallbacksById[call.arguments["id"]]!(value);
          } catch (e) {
            _nodeInstalledAppCallbacksById[call.arguments["id"]]!(call.arguments["args"]);
          }
        } else {
          _nodeInstalledAppCallbacksById[call.arguments["id"]]!(call.arguments["args"]);
        }
        break;

      case 'openPlayStoreInWearable':
        if (call.arguments["args"] is String) {
          try {
            Map? value = json.decode(call.arguments["args"]);
            _nodeInstalledAppCallbacksById[call.arguments["id"]]!(value);
          } catch (e) {
            _nodeInstalledAppCallbacksById[call.arguments["id"]]!(call.arguments["args"]);
          }
        } else {
          _nodeInstalledAppCallbacksById[call.arguments["id"]]!(call.arguments["args"]);
        }
        break;

      default:
        if (kDebugMode) {
          print(
            'TestFairy: Ignoring invoke from native. This normally shouldn\'t happen.');
        }
    }
  }



  /// register a callback function for wearable messages
  static Future<void> listenForMessage(MultiUseCallback callback) async {
    _channel.setMethodCallHandler(_methodCallHandler);
    int currentListenerId = _nextCallbackId++;
    _messageCallbacksById[currentListenerId] = callback;
    await _channel.invokeMethod("listenMessages", currentListenerId);
  }

  /// return a function listener for paired devices
  static Future<void> listenAvailableNodes(MultiUseCallback callback) async {
    _channel.setMethodCallHandler(_methodCallHandler);
    int currentListenerId = _nextCallbackId++;
    _availableNodesById[currentListenerId] = callback;
    await _channel.invokeMethod("listenDevices", currentListenerId);
  }
}