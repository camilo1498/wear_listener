// To parse this JSON data, do
//
//     final wearResponse = wearResponseFromJson(jsonString);

import 'dart:convert';

WearResponseModel wearResponseFromJson(String str) => WearResponseModel.fromJson(json.decode(str));

String wearResponseToJson(WearResponseModel data) => json.encode(data.toJson());

class WearResponseModel {
  String? id;
  String? name;
  bool connected;
  bool isInstall;

  WearResponseModel({
    this.id,
    this.name,
    this.connected = false,
    this.isInstall = false
  });

  factory WearResponseModel.fromJson(Map<String, dynamic> json) => WearResponseModel(
    id: json["id"] ?? 'no data',
    name: json["name"] ?? 'no data',
    connected: json["connected"] == 'true' ? true : false,
    isInstall: json["isInstall"] == 'true' ? true : false,
  );

  Map<String, dynamic> toJson() => {
    "id": id,
    "name": name,
    "connected": connected,
    "isInstall": isInstall
  };
}
