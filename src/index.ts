import ExpoSerialportModule from "./ExpoSerialportModule";
import UsbportModule from "./UsbportModule";

export interface UsbDevice {
  vendorId: number;
  deviceId: number;
  productId: number;
  deviceName: string;
  productName: string;
  deviceClass: number;
  deviceProtocol: number;
  interfaceCount: number;
  manufacturerName: string;
}

export function listDevices(): UsbDevice[] {
  return ExpoSerialportModule.listDevices();
}

export function listDevicesAgain(): UsbDevice[] {
  console.log("Entro a funcion listDevicesAgain");
  return ExpoSerialportModule.listDevicesAgain();
}

export function getSerialNumberAsync(deviceId: number): Promise<string> {
  return ExpoSerialportModule.getSerialNumberAsync(deviceId);
}

export function hasPermissionAsync(deviceId: number): Promise<boolean> {
  return ExpoSerialportModule.hasPermissionAsync(deviceId);
}

export function requestPermissionAsync(deviceId: number): Promise<void> {
  return ExpoSerialportModule.requestPermissionAsync(deviceId);
}

export function testLog() {
  return UsbportModule.testLog();
}

// export function write(deviceId: number, hexData: string): Promise<any> {
//   return ExpoSerialportModule.write(deviceId, hexData);
// }

export function write(deviceId: number): Promise<any> {
  console.log("Entro a funcion write");
  return ExpoSerialportModule.write(deviceId);
}

export default {
  listDevices,
  listDevicesAgain,
  hasPermissionAsync,
  getSerialNumberAsync,
  requestPermissionAsync,
  write,
  testLog
};
