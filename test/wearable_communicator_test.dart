import 'package:flutter_test/flutter_test.dart';
import 'package:wearable_communicator/wearable_communicator.dart';
import 'package:wearable_communicator/wearable_communicator_platform_interface.dart';
import 'package:wearable_communicator/wearable_communicator_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockWearableCommunicatorPlatform 
    with MockPlatformInterfaceMixin
    implements WearableCommunicatorPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final WearableCommunicatorPlatform initialPlatform = WearableCommunicatorPlatform.instance;

  test('$MethodChannelWearableCommunicator is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelWearableCommunicator>());
  });

  test('getPlatformVersion', () async {
    WearableCommunicator wearableCommunicatorPlugin = WearableCommunicator();
    MockWearableCommunicatorPlatform fakePlatform = MockWearableCommunicatorPlatform();
    WearableCommunicatorPlatform.instance = fakePlatform;
  
    expect(await wearableCommunicatorPlugin.getPlatformVersion(), '42');
  });
}
