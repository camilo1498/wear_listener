import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'wearable_communicator_platform_interface.dart';

/// An implementation of [WearableCommunicatorPlatform] that uses method channels.
class MethodChannelWearableCommunicator extends WearableCommunicatorPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('wearable_communicator');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }
}
