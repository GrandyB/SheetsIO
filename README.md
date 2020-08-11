# SheetsIO
[![license: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![GitHub release](https://img.shields.io/github/release/GrandyB/SheetsIO.svg)](https://github.com/GrandyB/SheetsIO/releases)

The aim of the project is to create a simple way to use Google Spreadsheet data as files within OBS.

Microsoft Excel (and other local sheet programs) often have the capability to record macros and save to files, however this requires some technical knowledge to do so, and requires the right type of sheet program  (as each have their own language for writing macros, sometimes making it difficult to share).

There's also a project or two such as [RewindRL](https://github.com/rewindrl/updater)'s updater system, which goes from Google Sheets into browser-based graphics.

SheetsIO is possibly (?) the first to go straight to files on your local system.

Want to help make this tool as useful as it could be? Send your bug reports/feature requests in the [issues tab](https://github.com/GrandyB/SheetsIO/issues).

## Config

Configs can be placed anywhere, selected through the file chooser in the UI.

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
- `"worksheetName"` - the sheet within the spreadsheet to observe
- `"apiKey"` - from Google Developer Console, more detail below
- `"cells"` - the configuration of cells to files

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
- `"pad"` _[optional]_ - if using a 'Text' type, you can set this value to append this number of spaces to the end of your text. e.g. `"5"` will convert `"hello"` -> `"hello     "` - useful for marquee-type text you wish to use in conjunction with the 'scroll' filter in OBS

Valid `fileExtension` values:
- Images: "png", "jpg", "jpeg", "gif", "bmp"
- Text: "txt"
- Video: "webm"