import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:wearable_communicator_example/src/presentation/pages/home/home_controller.dart';
import 'package:wearable_communicator_example/src/presentation/widgets/card_widget.dart';

class HomePage extends StatelessWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final ScreenUtil screenUtil = ScreenUtil();
    return GetBuilder<HomeController>(
      id: "home_page",
      init: HomeController(),
      builder: (_) => Scaffold(
        backgroundColor: Colors.white.withOpacity(0.97),
        appBar: AppBar(
          actions: [
            IconButton(
              onPressed: () async {
                await _.getAllConnectedAndInstalledApp();
              },
              icon: const Icon(Icons.refresh),
            )
          ],
        ),
        body: SafeArea(
          child: SingleChildScrollView(
            child: Padding(
              padding: EdgeInsets.symmetric(horizontal: 80.w, vertical: 30.h),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  30.verticalSpace,

                  /// title
                  const Text(
                    "Connected Nodes",
                    style: TextStyle(
                      color: Colors.black,
                      fontSize: 20,
                      fontWeight: FontWeight.bold
                    ),
                  ),

                  80.verticalSpace,
                  /// node list
                  ..._.allConnectedAndInstalledNodes.map((device) => Padding(
                    padding: const EdgeInsets.all(8.0),
                    child: WearInfoCard(
                      onTap: !device.isInstall ? () => _.openPlayStoreOnWearable(id: device.id.toString()) : null,
                      deviceId: device.id.toString(),
                      name: device.name.toString(),
                      isConnected: device.connected,
                      hasInstalledApp: device.isInstall,
                    )
                  ))
                ],
              ),
            ),
          ),
        ),
      ),
    );
  }
}