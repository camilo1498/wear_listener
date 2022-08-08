class MessageReceivedResponseModel {

  String path;
  String size;
  String data;

  MessageReceivedResponseModel({
    this.path = '',
    this.size = '',
    this.data = '',
  });

  factory MessageReceivedResponseModel.fromJson(Map<String, dynamic> json) => MessageReceivedResponseModel(
    path: json["path"] ?? '',
    size: json["size"] ?? '',
    data: json["data"] ?? '',
  );

  Map<String, dynamic> toJson() => {
    "path": path,
    "size": size,
    "data": data,
  };
}