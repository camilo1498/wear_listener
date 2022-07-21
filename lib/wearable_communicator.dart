
import 'wearable_communicator_platform_interface.dart';

class WearableCommunicator {
  Future<String?> getPlatformVersion() {
    return WearableCommunicatorPlatform.instance.getPlatformVersion();
  }
}
