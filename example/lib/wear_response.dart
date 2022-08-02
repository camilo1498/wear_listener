// To parse this JSON data, do
//
//     final wearResponse = wearResponseFromJson(jsonString);

import 'dart:convert';

WearResponse wearResponseFromJson(String str) => WearResponse.fromJson(json.decode(str));

String wearResponseToJson(WearResponse data) => json.encode(data.toJson());

class WearResponse {
  String? id;
  String? name;
  bool connected;

  WearResponse({
    this.id,
    this.name,
    this.connected = false,
  });

  factory WearResponse.fromJson(Map<String, dynamic> json) => WearResponse(
    id: json["id"] ?? 'no data',
    name: json["name"] ?? 'no data',
    connected: json["connected"] == 'true' ? true : false,
  );

  Map<String, dynamic> toJson() => {
    "id": id,
    "name": name,
    "connected": connected,
  };
}
