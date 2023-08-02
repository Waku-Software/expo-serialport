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
export declare function listDevices(): UsbDevice[];
export declare function getSerialNumberAsync(deviceId: number): Promise<string>;
export declare function hasPermissionAsync(deviceId: number): Promise<boolean>;
export declare function requestPermissionAsync(deviceId: number): Promise<void>;
export declare function write(deviceId: number): Promise<any>;
declare const _default: {
    listDevices: typeof listDevices;
    hasPermissionAsync: typeof hasPermissionAsync;
    getSerialNumberAsync: typeof getSerialNumberAsync;
    requestPermissionAsync: typeof requestPermissionAsync;
    write: typeof write;
};
export default _default;
//# sourceMappingURL=index.d.ts.map