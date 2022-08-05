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
    savedMessage = _prefs.getString('saved_message')!;
    savedNodeId = _prefs.getString('saved_node_id')!;
    getAllConnectedAndInstalledApp();

    /// listen realtime connection
    WearableListener.listenAvailableNodes((msg) async{
      await getAllConnectedAndInstalledApp();
    });
  }

  /// instances
  late SharedPreferences _prefs;

  /// variables
  List<WearResponseModel> allConnectedAndInstalledNodes = [];

  bool loading = false;

  String messageText = "";
  String savedMessage = "";
  String savedNodeId = "";

  saveMessageToLocalStorage() async{
    if(savedNodeId.isNotEmpty && messageText.isNotEmpty) {
      _prefs.setString('saved_message', messageText);
      update(['home_page']);
      WearableCommunicator.sendMessage(
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