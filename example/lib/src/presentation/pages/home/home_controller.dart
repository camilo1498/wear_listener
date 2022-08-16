import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:wearable_communicator/wearable_communicator.dart';
import 'package:wearable_communicator_example/src/data/models/wear_response.dart';

class HomeController extends GetxController {

  @override
  void onInit() async{
    super.onInit();
    _prefs = await SharedPreferences.getInstance();
    savedMessage = _prefs.getString('saved_message') ?? '';
    savedNodeId = _prefs.getString('saved_node_id') ?? '';
    /// listen realtime connection
    WearableListener.listenAvailableNodes((msg) async{
      /// get nodes
      await getAllConnectedAndInstalledApp();
    });

    WearableListener.listenForMessage((msg) {
      messageReceived = MessageReceivedResponseModel.fromJson(jsonDecode(msg));
      update(['home_page']);
    });
  }

  @override
  void onReady() async{
    super.onReady();
    /// get nodes
    await getAllConnectedAndInstalledApp();
  }

  /// instances
  late SharedPreferences _prefs;
  MessageReceivedResponseModel? messageReceived;

  /// variables
  List<WearResponseModel> allConnectedAndInstalledNodes = [];

  bool loading = false;

  String messageText = "";
  String savedMessage = "";
  String savedNodeId = "";

  sendToDatalayer() async{
    WearableCommunicator.sentDataToWear("dataClient", {
      "image": "test",
      "title": "title",
      "duration": "100",
      "repeats": "10",
      "num_exercises": "3000",
    });
  }

  openWearActivity() async{
    WearableCommunicator.sendMessage(
        path: "/start-sessions_activity",
        nodeID: savedNodeId,
        data: {
          "text": savedMessage
        }
    );
  }

  saveMessageToLocalStorage() async{
    if(messageText.isNotEmpty) {
      await _prefs.setString('saved_message', messageText);
      savedMessage = messageText;
      update(['home_page']);
    }
  }

  sendTokenToWear() {
    if(savedNodeId.isNotEmpty && savedMessage.isNotEmpty) {
      WearableCommunicator.sendMessage(
          path: "/token",
          nodeID: savedNodeId,
          data: {
            "text": savedMessage
          }
      );
    }
  }

  onChangeTextField(String text) {
    messageText = text;
    update(['home_page']);
  }

  selectDevice({required String id}) {
    _prefs.setString("saved_node_id", id);
    savedNodeId = _prefs.getString('saved_node_id').toString();
    update(["home_page"]);
  }

  getAllConnectedAndInstalledApp() async{
    try {
      loading = true;
      update(["home_page"]);
      final map = await WearableCommunicator.getAllConnectedAndInstalledApp();
      List<WearResponseModel> localRes = [];
      map.map((e) {
        localRes.add(WearResponseModel.fromJson(Map<String, dynamic>.from(e)));
      }).toList();

      allConnectedAndInstalledNodes = localRes;
      loading = false;
      update(["home_page"]);
    } on FlutterError catch(e) {
      debugPrint("flutter error: ${e.message}");
    }
  }

  openPlayStoreOnWearable({required String id}) async{
    await WearableCommunicator.openPlayStoreInWearable(nodeId: id, marketId: "wearablesoftware.wearspotifyplayer");
  }

}