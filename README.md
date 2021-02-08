# WiFiP2PMacBugReport

This is a demo for bug report: https://issuetracker.google.com/issues/179471511
Running this code on device with Android 11, doesn't produce expected results.

- WiFiP2P group manager address can't be retreived
- This sample also demonstrates that LocalOnlyHotspot BSSID can't be retreived either, albeit the method getSoftApConfiguration().getBssid() was added in SDK30
