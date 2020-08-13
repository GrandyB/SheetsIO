# SheetsIO
[![license: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![GitHub release](https://img.shields.io/github/release/GrandyB/SheetsIO.svg)](https://github.com/GrandyB/SheetsIO/releases)

![Image of SheetsIO](https://github.com/GrandyB/SheetsIO/blob/master/.github/ui_screenshot.png)

The aim of the project is to create a simple way to use Google Spreadsheet data as files within OBS.

Primary use case is for broadcasting with OBS - a common issue as a producer is having a ton of raw data (caster details, teams, scores etc) and a lack of a quick/easy way to put that information on screen. SheetsIO, with some basic config to map cell references to file names, you can bring text, images and even webms from the web, onto your local file system and thus easily into your OBS productions using default OBS sources, updating it all from the comfort of a Google Sheet!

There's other ways to do this of course - Microsoft Excel (and other local sheet programs) often have the capability to record macros and save to files, however this requires some technical knowledge to do so, and requires the right type of spreadsheet program (as each have their own language for writing macros, sometimes making it difficult to share). There's also project(s) such as [RewindRL](https://github.com/rewindrl/updater)'s updater system, which brings Google Sheets data into browser-based graphics. SheetsIO is possibly (?) the first to go straight to files on your local system however.

Want to help make this tool as useful as it could be? Try it out, break it! Send all your bug reports and feature requests in the [issues tab](https://github.com/GrandyB/SheetsIO/issues).

# Getting started

1. Get your [API key](#apikey) from Google
1. Create your Google Spreadsheet and enable "anyone with the link can view", grab its [spreadsheet ID](#spreadsheetid)
1. Setup your [config](#config) with your API key cell/file references
1. Load up the app, choose your config and either hit 'update now' or tick 'autoupdate'

Any issues, refer to the [troubleshooting](#troubleshooting) section.

## Config

Configs are json files and can be placed anywhere, selected through the file chooser in the UI.

```json
{
	"projectName": "myProject",
	"worksheetName": "Sheet1",
	"apiKey": "get_your_key_from_https://console.developers.google.com/",
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
- `"worksheetName"` - the sheet/tab within the spreadsheet to observe
- `"apiKey"` - from Google Developer Console - [see below](#apikey)
- `"spreadsheetId"` - from part of the URL you use to access your spreadsheet - [see below](#spreadsheetid)
- `"cells"` - the configuration of cells to files - [see below](#cells)

### apiKey
API key is required as of v4 of the Google Sheets API.

1. Go to https://console.developers.google.com/
1. 'Select a project' in the top bar, 'New Project'
1. Give it a name (`SheetsIO-integration` ?) and create it
1. View the project; on the dashboard there should be a 'Go to APIs overview'
1. Under the 'Credentials' tab, and click 'Create Credentials' at the top -> 'API key'

It should now display an API key for you to then use in your application!
It is advised (although completely optional) to then 'restrict' that key by IP address, or at least specifically to the sheets API - can do that through the key's settings easily at any time.

### spreadsheetId
For a sheet with the following URL:
```https://docs.google.com/spreadsheets/d/12YrqfVJENT6FJZB5NZrv2fHKg36XEy2jTE5X-mwC61g/edit#gid=0```

The 'id' is that middle section:
```12YrqfVJENT6FJZB5NZrv2fHKg36XEy2jTE5X-mwC61g```

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

## JsonSyntaxException / MalformedJsonException 
e.g. **com.google.gson.JsonSyntaxException: com.google.gson.stream.MalformedJsonException: Unterminated array at line 14 column 4 path $.cells[7]**

This one is pretty descriptive! Your json file is invalid - even tells you where the issue lies - line number, column number, which array value it's at!
If you still have issues, remove your apiKey from the file (replace with "") and run it through a service like [JSONLint](https://jsonlint.com/).

## java.io.IOException: Server returned HTTP response code: 403
HTTP 403 is a HTTP status code meaning access to the requested resource is forbidden. There's a ticket open for improving this error message to users.
The main cause of 403's is that your Google Spreadsheet is somehow unaccessible or you've used the incorrect [spreadsheetId](#spreadsheetid) - ensure that your Google Spreadsheet has been shared so that 'anyone with the link can view'.

## Something else?
Report any recurring bugs (with log files) in the [issues tab](https://github.com/GrandyB/SheetsIO/issues), or contact me on Discord (Grandy#0243) or [Twitter](https://twitter.com/GrandyB93).