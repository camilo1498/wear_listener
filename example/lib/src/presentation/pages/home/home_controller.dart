import 'package:flutter/material.dart';
import 'package:get/get.dart';
import 'package:wearable_communicator/wearable_communicator.dart';
import 'package:wearable_communicator_example/src/data/models/wear_response.dart';

class HomeController extends GetxController {

  @override
  void onInit() {
    super.onInit();
    getAllConnectedAndInstalledApp();

    /// listen realtime connection
    WearableListener.listenAvailableNodes((msg) async{
      await getAllConnectedAndInstalledApp();
    });
  }

  /// variables
  List<WearResponseModel> allConnectedAndInstalledNodes = [];

  bool loading = false;

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

  openPlayStoreOnWearable({required String id}) {
    WearableCommunicator.openPlayStoreInWearable({"node_id": id});
  }

}