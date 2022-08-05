import 'package:get/route_manager.dart';
import 'package:wearable_communicator_example/src/presentation/pages/home/home_binding.dart';
import 'package:wearable_communicator_example/src/presentation/pages/home/home_page.dart';
import 'package:wearable_communicator_example/src/presentation/routes/app_routes.dart';

abstract class AppPages {
  static List<GetPage<dynamic>>? get pages => [
    GetPage(
      name: AppRoutes.homePage,
      page: () => const HomePage(),
      binding: HomeBinding()
    )
  ];
}