import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:wearable_communicator_example/src/presentation/widgets/animations/animated_ontap_widget.dart';

class WearInfoCard extends StatelessWidget {
  final String name;
  final String deviceId;
  final bool isConnected;
  final bool isSelected;
  final bool hasInstalledApp;
  final Function()? onTapCheck;
  final Function()? onTapInstall;

  const WearInfoCard({
    Key? key,
    required this.deviceId,
    required this.name,
    required this.hasInstalledApp,
    this.onTapInstall,
    required this.isConnected,
    this.onTapCheck,
    this.isSelected = false
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(10),
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.3),
            blurRadius: 0.1,
            spreadRadius: 0.1,
            offset: const Offset(0, 1)
          )
        ]
      ),
      child: Container(
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(10),
        ),
        child: Padding(
          padding: EdgeInsets.symmetric(horizontal: 50.w, vertical: 30.h),
          child: Row(
            mainAxisSize: MainAxisSize.max,
            crossAxisAlignment: CrossAxisAlignment.center,
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Row(
                crossAxisAlignment: CrossAxisAlignment.center,
                mainAxisSize: MainAxisSize.min,
                children: [
                  AnimatedOnTapWidget(
                    onTap: onTapCheck,
                    child: Container(
                      width: 40.w,
                      height: 40.w,
                      decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(2),
                          border: Border.all(
                              color: Colors.black.withOpacity(0.8)
                          )
                      ),
                      child: isSelected ? const Icon(
                        Icons.check,
                        color: Colors.red,
                        size: 13,
                      ) : const SizedBox(),
                    ),
                  ),
                  30.horizontalSpace,

                  Column(
                    mainAxisSize: MainAxisSize.min,
                    mainAxisAlignment: MainAxisAlignment.center,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        name,
                        style: const TextStyle(
                            fontWeight: FontWeight.bold,
                            color: Colors.black,
                            fontSize: 13
                        ),
                      ),
                      15.verticalSpace,
                      Text(
                        isConnected ? "Conectado" : "Desconectado",
                        style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: isConnected ? Colors.green : Colors.red,
                            fontSize: 13
                        ),
                      ),
                    ],
                  ),
                ],
              ),
              if(hasInstalledApp)
                SizedBox(
                  width: 50.w,
                  height: 50.w,
                  child: const Image(
                   image:  AssetImage("assets/icons/google_play.png"),
                  ),
                )
              else
                AnimatedOnTapWidget(
                  onTap: onTapInstall ?? (){},
                  child: SizedBox(
                    width: 50.w,
                    height: 50.w,
                    child: const Image(
                      image:  AssetImage("assets/icons/download.png"),
                    ),
                  ),
                )
            ],
          ),
        ),
      ),
    );
  }
}