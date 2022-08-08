// ignore_for_file: file_names, library_private_types_in_public_api
import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class AnimatedOnTapWidget extends StatefulWidget {
  final Widget child;
  final Function()? onTap;
  final bool? enabled;
  final Function()? onLongPress;

  const AnimatedOnTapWidget(
      {Key? key,
        this.onTap,
        required this.child,
        this.enabled = true,
        this.onLongPress,
      })
      : super(key: key);

  @override
  _AnimatedOnTapWidgetState createState() => _AnimatedOnTapWidgetState();
}

class _AnimatedOnTapWidgetState extends State<AnimatedOnTapWidget>
    with TickerProviderStateMixin {
  double squareScaleA = 1;
  AnimationController? _controllerA;
  Timer _timer = Timer(const Duration(milliseconds: 300), () {});

  @override
  void initState() {
    if (mounted) {
      _controllerA = AnimationController(
        vsync: this,
        lowerBound: 0.95,
        upperBound: 1.0,
        value: 1,
        duration: const Duration(milliseconds: 10),
      );
      _controllerA?.addListener(() {
        setState(() {
          squareScaleA = _controllerA!.value;
        });
      });
      super.initState();
    }
  }

  @override
  void dispose() {
    if (mounted) {
      _controllerA!.dispose();
      _timer.cancel();
      super.dispose();
    }
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      behavior: HitTestBehavior.translucent,
      onTap:  widget.onTap != null && widget.enabled! ? () {
        /// set vibration
        HapticFeedback.lightImpact();
        _controllerA!.reverse();
        widget.onTap!();
      } : (){},
      onTapDown: (dp) => widget.enabled! ? _controllerA!.reverse() : null,
      onTapUp: (dp) => widget.enabled! && mounted
          ? _timer = Timer(const Duration(milliseconds: 100), () => _controllerA!.fling())
          : null,
      onTapCancel: () => widget.enabled! ?  _controllerA!.fling() : null,
      child: Transform.scale(
        scale: squareScaleA,
        child: widget.child,
      ),
    );
  }
}