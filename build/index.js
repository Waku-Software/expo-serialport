import ExpoSerialportModule from "./ExpoSerialportModule";
export function listDevices() {
    return ExpoSerialportModule.listDevices();
}
export function getSerialNumberAsync(deviceId) {
    return ExpoSerialportModule.getSerialNumberAsync(deviceId);
}
export function hasPermissionAsync(deviceId) {
    return ExpoSerialportModule.hasPermissionAsync(deviceId);
}
export function requestPermissionAsync(deviceId) {
    return ExpoSerialportModule.requestPermissionAsync(deviceId);
}
// export function write(deviceId: number, hexData: string): Promise<any> {
//   return ExpoSerialportModule.write(deviceId, hexData);
// }
export function write(deviceId) {
    console.log("Entro a funcion write");
    return ExpoSerialportModule.write(deviceId);
}
export default {
    listDevices,
    hasPermissionAsync,
    getSerialNumberAsync,
    requestPermissionAsync,
    write
};
//# sourceMappingURL=index.js.map