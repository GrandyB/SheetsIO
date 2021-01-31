# SheetsIO
[![license: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![GitHub release](https://img.shields.io/github/release/GrandyB/SheetsIO.svg)](https://github.com/GrandyB/SheetsIO/releases)

![Image of SheetsIO](https://github.com/GrandyB/SheetsIO/blob/master/.github/ui_screenshot.png)

**Brief:**
Use Google Sheets as a central and easily updateable backend for all text/images/files in productions.

**More:**

A common issue as a producer is having an unruly mass of raw data - caster names/socials/photos, team names/logos/scores, player names/stats etc - without any tools it's a nightmare to setup and maintain. SheetsIO, with some basic config to map cell references to file names, allows you to bring down text, images and even webms from the web onto your local file system, and thus easily into your OBS productions using regular ol' OBS sources... all from the comfort of a Google Sheet!

There's other tools/methods out there that attempt to address this problem - Microsoft Excel (and other spreadsheet programs) often have the capability to record macros and save data to files, however this requires some technical knowledge to create, and requires the right type of spreadsheet program to use (as MS/Libre/Openoffice each their own language for writing macros, often making it difficult to share). Then there's project(s) such as [RewindRL](https://github.com/rewindrl/updater)'s updater system, which brings Google Sheets data into browser-based graphics, which while powerful, requires a fair bit of coding knowledge to use properly. SheetsIO on the other hand requires zero coding knowledge to implement, uses shareable configs, can bring in an unlimited amount of data of various types, allows for remote editing of data on Google Sheets, and is generally very simple to use.

Want to help make this tool as useful as it could be? Try it out - break it! Write up all your bug reports and feature requests in the [issues tab](https://github.com/GrandyB/SheetsIO/issues).

# Getting started

1. [Get your API key from Google](#google-sheets-api-key) and enable it
1. Create your Google Spreadsheet and enable "anyone with the link can view", grab its [spreadsheet ID](#spreadsheetid)
1. Setup your [config](#config) with your spreadsheet ID/workbook name and cell/file references
1. Load up the app, put your API key, choose your config and either hit 'update now' or tick 'autoupdate'

Any issues, refer to the [troubleshooting](#troubleshooting) section.

# Google Sheets API key
SheetsIO uses the Google Sheets v4 API; unlike previous version of the API, this one requires the use of an API key that you have to generate using Google's developer console.
To do this, you can either create a project and then enable the Google Sheets API on that project _or_ search for the API and enable it (which will automatically create you a project).

1. Go to https://console.developers.google.com/
1. Create a new project; this can be done in multiple ways but here's one:
    1. In the upper left 'Select a project' drop down, open it up and use 'New Project'
    1. Give it a name (`SheetsIO-integration` ?) and create it (this may take a moment)
1. Navigate through 'Credentials' in the left-side menu, click 'Create Credentials' at the top and choose 'API key'

It should now display an API key for you to then use in your application! It's important to keep this secure, so whenever sharing configs, ensure you remove your apiKey.
It is advised (although completely optional) to then 'restrict' that key by IP address, or at least specifically to the sheets API - can do that through the key's settings easily at any time.

## Config

Configs are json files and can be placed anywhere, selected through the file chooser in the UI.

```json
{
	"projectName": "myProject",
	"worksheetName": "Sheet1",
	"spreadsheetId": "12YrqfVJENT6FJZB5NZrv2fHKg36XEy2jTE5X-mwC61g",
	"cells": [
		{ "cell": "B2", "name": "team1Name" },
		{ "cell": "B3", "name": "team1Logo", "fileExtension": "png" },
		{ "cell": "B4", "name": "team2Name" },
		{ "cell": "B5", "name": "team2Logo", "fileExtension": "png" },
		{ "cell": "B6", "name": "marqueeText", "pad": "15" }
	]
}
```

- `"projectName"` - becomes the folder name; files are generated within `/files/projectName`
- `"worksheetName"` - sheets API only allows a single 'tab' within your worksheet to read, so this is the name of that tab in the lower bar
- `"spreadsheetId"` - from part of the URL you use to access your spreadsheet - [see below](#spreadsheetid)
- `"cells"` - the configuration of cells to files - [see below](#cells)

### spreadsheetId
For a sheet with the following URL:
```https://docs.google.com/spreadsheets/d/12YrqfVJENT6FJZB5NZrv2fHKg36XEy2jTE5X-mwC61g/edit#gid=0```

The 'id' is that middle section:
```12YrqfVJENT6FJZB5NZrv2fHKg36XEy2jTE5X-mwC61g```

Remember that the spreadsheet's sharing settings must be open so that 'anyone with the link can view' in order for the system to connect.

### cells
Each cell is formed of:

- `"cell"` - the alphanumeric excel-style cell reference, e.g. `"A4"`
- `"name"` - what this cell represents - also used as the first part of the file name, e.g. `"team1Name"`
- `"fileExtension"` _[optional]_ - the file extension, which in turn becomes the file type. System assumes the cell is `"txt"` if not optionally given
- `"pad"` _[optional]_ - if using a 'Text' type, this number of spaces will be added to end of your text. e.g. `"15"` will add 15 spaces to the end - useful for marquee-type text you wish to use in conjunction with the 'scroll' filter in OBS

Valid `fileExtension` values:
- Images: "png", "jpg", "jpeg", "gif", "bmp"
- Text: "txt"
- Video: "webm"

## Timer

The timer is a combination of spinners and buttons to control `/files/timer.txt`, which updates each second that the timer is active.

- `Start` button begins the countdown, regardless of values
- `Update` button forces the countdown to the values in the box; if the timer is running, it'll then continue ticking down but from the new value, if the timer is paused, it'll set this new value but not resume automatically
- `Reset` button forces the countdown to its starting 00:00 display but does not affect the spinners

# Troubleshooting
Encountering issues? Hopefully the system is providing error message(s) in the UI, if not, check the `/logs` folder!

### JsonSyntaxException / MalformedJsonException 
e.g. **com.google.gson.JsonSyntaxException: com.google.gson.stream.MalformedJsonException: Unterminated array at line 14 column 4 path $.cells[7]**

This one is pretty descriptive! Your json file is invalid - even tells you where the issue lies - line number, column number, which array value it's at!
If you still have issues, try running your config through a service like [JSONLint](https://jsonlint.com/).

### 403 - PERMISSION_DENIED - "API has not been used in project before or is disabled"
This means that the key you're using is "correct", but isn't enabled on your project in Google.
Refer to the [api key section](#google-sheets-api-key) (specifically step #5) to fix.

### 403 - PERMISSION_DENIED - "The caller does not have permission"
Ensure that your Google Spreadsheet has been shared so that 'anyone with the link can view'.

### 'no protocol' error message
If you're using a fileExtension (e.g. using images, videos) and the cell you specify in the config does _not_ contain a link to media of an applicable type on the sheet, this error message may appear.

e.g. if your config is `{ "cell": "A1", "name": "anImage", "fileExtension": "png" }`" and the actual contents of A1 is 'thisisnotanimage', you'd get the error `no protocol: thisisnotanimage`, because it is attempting to connect to (what it expects to be) a hyperlink and failing.

`no protocol: #N/A` is a common one, as `#N/A` is sheets' common lookup error that it puts in the cell. In Google Sheets, you can add error handling to cases like these, e.g. `=IFNA(VLOOKUP_HERE,)` - this way, if the lookup returns nothing, it uses the second parameter (and leaves the cell empty instead of putting `#N/A` in there).

### Something else?
Report any recurring bugs (with log files) in the [issues tab](https://github.com/GrandyB/SheetsIO/issues), or contact me on Discord (Grandy#0243) or [Twitter](https://twitter.com/GrandyB93).
