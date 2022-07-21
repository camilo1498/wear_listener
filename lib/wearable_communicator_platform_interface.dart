import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'wearable_communicator_method_channel.dart';

abstract class WearableCommunicatorPlatform extends PlatformInterface {
  /// Constructs a WearableCommunicatorPlatform.
  WearableCommunicatorPlatform() : super(token: _token);

  static final Object _token = Object();

  static WearableCommunicatorPlatform _instance = MethodChannelWearableCommunicator();

  /// The default instance of [WearableCommunicatorPlatform] to use.
  ///
  /// Defaults to [MethodChannelWearableCommunicator].
  static WearableCommunicatorPlatform get instance => _instance;
  
  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [WearableCommunicatorPlatform] when
  /// they register themselves.
  static set instance(WearableCommunicatorPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
