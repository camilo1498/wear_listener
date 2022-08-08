import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import 'package:wearable_communicator_example/src/presentation/widgets/animations/animated_ontap_widget.dart';

class ButtonWidget extends StatelessWidget {
  final Function()? onTap;
  final String title;
  const ButtonWidget({
    Key? key,
    this.onTap,
    required this.title
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final ScreenUtil screenUtil = ScreenUtil();
    return AnimatedOnTapWidget(
      onTap: onTap ?? (){},
      child: Container(
        width: (screenUtil.screenWidth / 2) - 100.w,
        padding: EdgeInsets.symmetric(horizontal: 40.w, vertical: 20.h),
        alignment: Alignment.center,
        decoration: BoxDecoration(
          color: Colors.red,
          borderRadius: BorderRadius.circular(10),
          boxShadow: [
            BoxShadow(
              color: Colors.black.withOpacity(0.2),
              offset: const Offset(0, 1),
              blurRadius: 0.1,
              spreadRadius: 0.1
            )
          ]
        ),
        child: Text(
          title,
          style: const TextStyle(
            color: Colors.white,
            fontWeight: FontWeight.bold,
            fontSize: 14,
          ),
        ),
      ),
    );
  }
}