import 'dart:math';

import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:get/route_manager.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:wearable_communicator/wearable_communicator.dart';
import 'package:wearable_communicator_example/src/data/models/wear_response.dart';
import 'package:wearable_communicator_example/src/presentation/routes/app_pages.dart';
import 'package:wearable_communicator_example/src/presentation/routes/app_routes.dart';

void main() {
  /// Make sure that the widget are already initialized
  WidgetsFlutterBinding.ensureInitialized();
  Paint.enableDithering = true;

  runApp(const MainPage());
}

class MainPage extends StatelessWidget {
  const MainPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return NotificationListener<OverscrollIndicatorNotification>(
      onNotification: (overscroll) {
        overscroll.disallowIndicator();
        return false;
      },
      child: ScreenUtilInit(
        designSize: const Size(1080, 1920),
        builder: (_, __) => GetMaterialApp(
          title: 'Wearable Communicator',
          debugShowCheckedModeBanner: false,
          theme: ThemeData(
              primarySwatch: Colors.red,
              useMaterial3: true),
          getPages: AppPages.pages,
          initialRoute: AppRoutes.homePage,
          defaultTransition: Transition.fadeIn,
        ),
      ),
    );
  }
}


// class MyApp extends StatefulWidget {
//   const MyApp({Key? key}) : super(key: key);
//
//   @override
//   _MyAppState createState() => _MyAppState();
// }
//
// class _MyAppState extends State<MyApp> {
//   TextEditingController? _controller;
//   String value = '';
//   String message = '';
//   String devices = '';
//   String key = '';
//   List<WearResponseModel> allConnectedNodes = [];
//   List<WearResponseModel> allNodesWithInstalledApp = [];
//   List<WearResponseModel> allConnectedAndInstalledNodes = [];
//   late SharedPreferences prefs;
//
//
//   @override
//   void initState() {
//     super.initState();
//     _init();
//     _controller = TextEditingController();
//     _getAllConnectedNodes();
//     _getAllNodesWithInstalledApp();
//     _getAllConnectedAndInstalledApp();
//     WearableListener.listenForMessage((msg) {
//       debugPrint('message $msg');
//       WearableCommunicator.sendMessage(
//         nodeID: "",
//         data: {"text": msg});
//       setState(() => message = msg);
//     });
//
//     WearableListener.listenAvailableNodes((msg) async{
//       debugPrint('flutter paired devices: $msg');
//       await _getAllConnectedNodes();
//       await _getAllNodesWithInstalledApp();
//       await _getAllConnectedAndInstalledApp();
//       setState(() => devices = msg);
//     });
//   }
//
//   _init() async{
//     prefs = await SharedPreferences.getInstance();
//   }
//
//   @override
//   void dispose() {
//     _controller!.dispose();
//     super.dispose();
//   }
//
//   _getAllConnectedNodes()async{
//     final map = await WearableCommunicator.getAllConnectedNodes();
//     print('============ all nodes');
//     print(map.toString());
//     print('============');
//     setState(() {
//       List<WearResponseModel> localRes = [];
//       map.map((e) {
//         localRes.add(WearResponseModel.fromJson(Map<String, dynamic>.from(e)));
//       }).toList();
//       allConnectedNodes = localRes;
//     });
//   }
//
//   _getAllNodesWithInstalledApp()async{
//     final map = await WearableCommunicator.getAllNodesWithInstalledApp();
//     print('============ installed app');
//     print(map.toString());
//     print('============');
//     setState(() {
//       List<WearResponseModel> localRes = [];
//       map.map((e) {
//         localRes.add(WearResponseModel.fromJson(Map<String, dynamic>.from(e)));
//       }).toList();
//       allNodesWithInstalledApp = localRes;
//     });
//   }
//
//   _getAllConnectedAndInstalledApp()async{
//     final map = await WearableCommunicator.getAllConnectedAndInstalledApp();
//     print('============ connected and installed app');
//     print(map.toString());
//     print('============');
//     setState(() {
//       List<WearResponseModel> localRes = [];
//       map.map((e) {
//         localRes.add(WearResponseModel.fromJson(Map<String, dynamic>.from(e)));
//       }).toList();
//       allConnectedAndInstalledNodes = localRes;
//     });
//   }
//
//   @override
//   Widget build(BuildContext context) {
//     return MaterialApp(
//       home: Scaffold(
//         appBar: AppBar(
//           title: const Text('Plugin example app'),
//           actions: [
//             IconButton(
//               onPressed: () async {
//                 await _getAllConnectedNodes();
//               },
//               icon: const Icon(Icons.refresh),
//             )
//           ],
//         ),
//         body: Center(
//           child: SingleChildScrollView(
//             physics: const BouncingScrollPhysics(),
//             child: Column(
//               mainAxisAlignment: MainAxisAlignment.start,
//               crossAxisAlignment: CrossAxisAlignment.center,
//               children: [
//                 const SizedBox(height: 30,),
//                 Text(
//                   allConnectedNodes[0].connected ? 'Wear connected' : 'Wear disconnected',
//                   style: TextStyle(
//                       fontWeight: FontWeight.bold,
//                       color: allConnectedNodes[0].connected ? Colors.green : Colors.red
//
//                   ),
//                 ),
//                 const SizedBox(height: 30,),
//                 Row(
//                   mainAxisAlignment: MainAxisAlignment.spaceEvenly,
//                   children: [
//                     _wearInfo(title: 'Device id', subTitle: allConnectedNodes[0].id != null ? allConnectedNodes[0].id! : ''),
//                     _wearInfo(title: 'Device name', subTitle: allConnectedNodes[0].name != null ? allConnectedNodes[0].name! : ''),
//                   ],
//                 ),
//                 const SizedBox(height: 30,),
//                 Padding(
//                   padding: const EdgeInsets.symmetric(horizontal: 40),
//                   child: TextField(
//                     onChanged: (text) => setState(() => key = text),
//                     decoration: const InputDecoration(
//                         hintText: 'Saved key',
//                         border: InputBorder.none
//                     ),
//                   ),
//                 ),
//                 MaterialButton(
//                   child: const Text('Save Key'),
//                   onPressed: () async{
//                     if(key != ''){
//                       //debugPrint(key);
//                       /// save in shared preferences
//                       await prefs.setString('wear', key);
//
//                       debugPrint('Saved Key: ${prefs.getString('wear')}');
//                     }
//                   },
//                 ),
//                 const SizedBox(height: 30,),
//                 MaterialButton(
//                   child: const Text('Send token'),
//                   onPressed: () {
//                     primaryFocus!.unfocus(disposition: UnfocusDisposition.scope);
//                     if(allConnectedNodes[0].connected){
//                       WearableCommunicator.sendMessage(
//                          nodeID: "",
//                           data: {
//                         "text": Random.secure().nextDouble().toString()
//                       });
//                     }
//                   },
//                 ),
//
//                 const SizedBox(height: 30,),
//                 Container(
//                     alignment: Alignment.center,
//                     child: Text(
//                       message,
//                       style: const TextStyle(
//                           color: Colors.black
//                       ),
//                     )
//                 ),
//                 const SizedBox(height: 30,),
//                 Container(
//                     alignment: Alignment.center,
//                     child:const  Text(
//                       "devices",
//                       style:  TextStyle(
//                           color: Colors.black
//                       ),
//                     )
//                 ),
//                 const SizedBox(height: 30,),
//                 Container(
//                     alignment: Alignment.center,
//                     child: Text(
//                       devices,
//                       style: const TextStyle(
//                           color: Colors.black
//                       ),
//                     )
//                 ),
//                 const SizedBox(height: 30,),
//                 Container(
//                     alignment: Alignment.center,
//                     child:const  Text(
//                       "devices with installed app",
//                       style:  TextStyle(
//                           color: Colors.black
//                       ),
//                     )
//                 ),
//                 const SizedBox(height: 30,),
//                 if(allNodesWithInstalledApp.isNotEmpty)
//                   ...allNodesWithInstalledApp.map((node) => Padding(
//                     padding: const EdgeInsets.symmetric(vertical: 5, horizontal: 30),
//                     child: Container(
//                       decoration: BoxDecoration(
//                           borderRadius: BorderRadius.circular(5),
//                           boxShadow: [
//                             BoxShadow(
//                                 color: Colors.black.withOpacity(0.3),
//                                 spreadRadius: 0.1,
//                                 blurRadius: 0.1,
//                                 offset: const Offset(0, 1)
//                             ),
//                           ]
//                       ),
//                       child: ListTile(
//                         title: Column(
//                           mainAxisAlignment: MainAxisAlignment.center,
//                           crossAxisAlignment: CrossAxisAlignment.start,
//                           children: [
//                             Text('id: ${node.id.toString()}'),
//                             Text('name: ${node.name.toString()}'),
//                             Text('connected: ${node.connected.toString()}'),
//                           ],
//                         ),
//                       ),
//                     ),
//                   )),
//
//                 const SizedBox(height: 30,),
//                 Container(
//                     alignment: Alignment.center,
//                     child:const  Text(
//                       "All connected devices",
//                       style:  TextStyle(
//                           color: Colors.black
//                       ),
//                     )
//                 ),
//                 const SizedBox(height: 30,),
//                 if(allConnectedNodes.isNotEmpty)
//                   ...allConnectedNodes.map((node) => Padding(
//                     padding: const EdgeInsets.symmetric(vertical: 5, horizontal: 30),
//                     child: Container(
//                       decoration: BoxDecoration(
//                         borderRadius: BorderRadius.circular(5),
//                         boxShadow: [
//                           BoxShadow(
//                             color: Colors.black.withOpacity(0.3),
//                             spreadRadius: 0.1,
//                             blurRadius: 0.1,
//                             offset: const Offset(0, 1)
//                           ),
//                         ]
//                       ),
//                       child: ListTile(
//                         onTap: (){
//                          // WearableCommunicator.openPlayStoreInWearable({"node_id": node.id.toString()});
//                         },
//                         title: Column(
//                           mainAxisAlignment: MainAxisAlignment.center,
//                           crossAxisAlignment: CrossAxisAlignment.start,
//                           children: [
//                             Text('id: ${node.id}'),
//                             Text('name: ${node.name}'),
//                             Text('connected: ${node.connected}'),
//                           ],
//                         ),
//                       ),
//                     ),
//                   )),
//
//                 const SizedBox(height: 30,),
//                 Container(
//                     alignment: Alignment.center,
//                     child:const  Text(
//                       "Merged nodes",
//                       style:  TextStyle(
//                           color: Colors.black
//                       ),
//                     )
//                 ),
//                 if(allConnectedAndInstalledNodes.isNotEmpty)
//                   ...allConnectedAndInstalledNodes.map((node) => Padding(
//                     padding: const EdgeInsets.symmetric(vertical: 5, horizontal: 30),
//                     child: Container(
//                       decoration: BoxDecoration(
//                           borderRadius: BorderRadius.circular(5),
//                           boxShadow: [
//                             BoxShadow(
//                                 color: Colors.black.withOpacity(0.3),
//                                 spreadRadius: 0.1,
//                                 blurRadius: 0.1,
//                                 offset: const Offset(0, 1)
//                             ),
//                           ]
//                       ),
//                       child: ListTile(
//                         onTap: (){
//                         //  WearableCommunicator.openPlayStoreInWearable({"node_id": node.id.toString()});
//                         },
//                         title: Column(
//                           mainAxisAlignment: MainAxisAlignment.center,
//                           crossAxisAlignment: CrossAxisAlignment.start,
//                           children: [
//                             Text('id: ${node.id}'),
//                             Text('name: ${node.name}'),
//                             Text('connected: ${node.connected}'),
//                             Text("isInstall ${node.isInstall}")
//                           ],
//                         ),
//                       ),
//                     ),
//                   ))
//               ],
//             ),
//           ),
//         )
//       ),
//     );
//   }
//
//   Widget _wearInfo({required String title, required String subTitle}){
//     return Column(
//       children: [
//         Text(
//           title,
//           style: const TextStyle(
//               fontWeight: FontWeight.bold,
//               color: Colors.green
//           ),
//         ),
//         const SizedBox(height: 5,),
//         Text(
//           subTitle,
//           style: const TextStyle(
//               fontWeight: FontWeight.w500,
//               fontSize: 12
//           ),
//         ),
//       ],
//     );
//   }
// }