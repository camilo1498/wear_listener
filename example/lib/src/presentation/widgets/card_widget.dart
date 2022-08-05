import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';

class WearInfoCard extends StatelessWidget {
  final String name;
  final String deviceId;
  final bool isConnected;
  final bool hasInstalledApp;
  final Function()? onTap;

  const WearInfoCard({
    Key? key,
    required this.deviceId,
    required this.name,
    required this.hasInstalledApp,
    required this.isConnected,
    this.onTap
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap ?? () {},
      child: Container(
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
                if(hasInstalledApp)
                  SizedBox(
                    width: 50.w,
                    height: 50.w,
                    child: const Image(
                     image:  AssetImage("assets/icons/google_play.png"),
                    ),
                  )
                else
                  SizedBox(
                    width: 50.w,
                    height: 50.w,
                    child: const Image(
                      image:  AssetImage("assets/icons/download.png"),
                    ),
                  )
              ],
            ),
          ),
        ),
      ),
    );
  }
}