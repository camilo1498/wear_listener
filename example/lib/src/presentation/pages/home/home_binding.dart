import 'package:get/get.dart';
import 'package:wearable_communicator_example/src/presentation/pages/home/home_controller.dart';

class HomeBinding implements Bindings {
  @override
  void dependencies() {
    Get.lazyPut(() => HomeController());
  }
}