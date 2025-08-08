import { CapacitorConfig } from "@capacitor/cli";

const config: CapacitorConfig = {
  appId: "com.emergent.frontend",
  appName: "EmergentFrontend",
  webDir: "build",
  bundledWebRuntime: false,
  server: {
    androidScheme: "https",
    cleartext: true
  }
};

export default config;