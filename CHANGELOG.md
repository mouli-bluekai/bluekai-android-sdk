## v2.0 (05.27.2015)
-----
### Bug Fix
- Fixed memory leak in which every time the resume method is called it does not spawn a new web view

### Enhancements
- Added option to make direct calls to tags server instead of using the web view
- All calls either using web view or direct call, would send the Advertising ID as adid parameter if ad tracking is not limited in Google Settings
- Removed dependency of the SDK on android support library

## v1.1 (06.12.2014)
-----

### Bug Fix
- Pop-up window works correctly now when devMode is set

### Enhancements
- Bumping up targetSdkVersion to 19
- Adding https mode for sending data to BlueKai
- Added getter and setter methods for various attributes
- Data payload to BlueKai doesn’t include identifierForVendor (IMEI info) anymore
- Call to BlueKai doesnt have TC parameter anymore
- Constructor for SettingsLayout now also takes a background color to set

### Deprecated
- put(Map<String, String>) method in favor of putAll(Map<String, String>) method.
- setOptIn(boolean) method in favor of setOptInPreference(boolean)


### v1.0.2 (05.03.2014)
-----
- Update mobile proxy end-point
- Add `CHANGELOG.md`


### v1.0.1
-----
- Mobile proxy end-point


### v1.0.0
-----
Initial SDK release.
- Initial release
- Pass individual hints or collections
