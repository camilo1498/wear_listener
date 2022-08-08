import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/get.dart';
import 'package:wearable_communicator_example/src/presentation/pages/home/home_controller.dart';
import 'package:wearable_communicator_example/src/presentation/widgets/button_widget.dart';
import 'package:wearable_communicator_example/src/presentation/widgets/card_widget.dart';

class HomePage extends StatelessWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final ScreenUtil screenUtil = ScreenUtil();
    return GetBuilder<HomeController>(
      id: "home_page",
      init: HomeController(),
      builder: (_) => Stack(
        children: [
          Scaffold(
            backgroundColor: Colors.white.withOpacity(0.97),
            appBar: AppBar(
              backgroundColor: Colors.red,
              title: const Text(
                "WearOs Communicator",
                style: TextStyle(
                    color: Colors.white,
                    fontSize: 20,
                    fontWeight: FontWeight.bold
                ),
              ),
              actions: [
                IconButton(
                  onPressed: () async {
                    await _.getAllConnectedAndInstalledApp();
                  },
                  icon: const Icon(Icons.refresh,color: Colors.white,),
                )
              ],
            ),
            body: SafeArea(
              child: SingleChildScrollView(
                child: Padding(
                  padding: EdgeInsets.symmetric(horizontal: 80.w),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      30.verticalSpace,
                      /// title
                      const Text(
                        "Send message to wear",
                        style: TextStyle(
                            color: Colors.black,
                            fontSize: 20,
                            fontWeight: FontWeight.bold
                        ),
                      ),
                      30.verticalSpace,

                      /// text field
                      Container(
                        padding: EdgeInsets.symmetric(vertical: 8.h, horizontal: 30.w),
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(10),
                          border: Border.all(
                            color: Colors.grey,
                            width: 1
                          )
                        ),
                        child: TextField(
                          onChanged: _.onChangeTextField,
                          decoration: const InputDecoration(
                              hintText: 'Local storage message',
                              border: InputBorder.none
                          ),
                        ),
                      ),
                      50.verticalSpace,
                      /// title
                      const Text(
                        "Saved token",
                        style: TextStyle(
                            color: Colors.black,
                            fontSize: 13,
                            fontWeight: FontWeight.bold
                        ),
                      ),
                      /// saved token
                      Text(
                        _.savedMessage,
                        style: const TextStyle(
                            color: Colors.black,
                            fontSize: 13,
                            fontWeight: FontWeight.w400
                        ),
                      ),
                      30.verticalSpace,

                      /// buttons
                      Row(
                        mainAxisSize: MainAxisSize.max,
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        crossAxisAlignment: CrossAxisAlignment.center,
                        children: [
                          ButtonWidget(
                            title: 'Save token',
                            onTap: _.saveMessageToLocalStorage,
                          ),
                          ButtonWidget(
                            title: 'Send message',
                            onTap: _.sendTokenToWear,
                          ),
                        ],
                      ),
                      60.verticalSpace,

                      /// title
                      const Text(
                        "Connected nodes",
                        style: TextStyle(
                            color: Colors.black,
                            fontSize: 20,
                            fontWeight: FontWeight.bold
                        ),
                      ),
                      30.verticalSpace,


                      /// node list
                      if(!_.loading)
                        ..._.allConnectedAndInstalledNodes.map((device) {
                          final int index = _.allConnectedAndInstalledNodes.indexOf(device);
                          return Padding(
                              padding: EdgeInsets.symmetric(vertical: 15.w),
                              child: WearInfoCard(
                                onTapCheck: () async =>_.selectDevice(id: device.id.toString()),
                                onTapInstall: !device.isInstall ? () async => _.openPlayStoreOnWearable(id: device.id.toString()) : null,
                                deviceId: device.id.toString(),
                                name: device.name.toString(),
                                isConnected: device.connected,
                                hasInstalledApp: device.isInstall,
                                isSelected: _.savedNodeId == device.id,
                              )
                          );
                        })
                      else
                        const Center(
                          child: CircularProgressIndicator(),
                        ),
                      60.verticalSpace,

                      /// title
                      const Text(
                        "Received Message",
                        style: TextStyle(
                            color: Colors.black,
                            fontSize: 20,
                            fontWeight: FontWeight.bold
                        ),
                      ),
                      30.verticalSpace,

                      /// title
                      Text(
                        "path = ${_.messageReceived != null ? _.messageReceived!.path : ''}",
                        style: const TextStyle(
                            color: Colors.black,
                            fontSize: 13,
                            fontWeight: FontWeight.w400
                        ),
                      ),
                      Text(
                        "message = ${_.messageReceived != null ? _.messageReceived!.data : ''}",
                        style: const TextStyle(
                            color: Colors.black,
                            fontSize: 13,
                            fontWeight: FontWeight.w400
                        ),
                      ),
                      Text(
                        "size = ${_.messageReceived != null ? _.messageReceived!.size : ''}",
                        style: const TextStyle(
                            color: Colors.black,
                            fontSize: 13,
                            fontWeight: FontWeight.w400
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}