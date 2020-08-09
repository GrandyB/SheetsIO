# SheeTXT
[![license: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![GitHub release](https://img.shields.io/github/release/GrandyB/SheeTXT.svg)](https://github.com/GrandyB/SheeTXT/releases)

The aim of the project is to create a simple way to use Google Spreadsheet data as text files within OBS.

Microsoft Excel (and other local sheet programs) often have the capability to record macros and save to files, however this requires some technical knowledge to do so, and requires the right type of sheet program  (as each have their own language for writing macros, sometimes making it difficult to share).

There's also a project or two such as [RewindRL](https://github.com/rewindrl/updater)'s updater system, which goes from Google Sheets into browser-based graphics.

SheeTXT is (possibly) the first to go straight to text files.

## Contributing

Check out [CONTRIBUTING.md](CONTRIBUTING.md) for details on dev setup - it's a pretty bog-standard Maven project; I use Eclipse.

Main thing right now is getting people to test the program and feed back on what's good/bad, what feature(s) would be great.
The hope is that with a few simple UI controls, there's a fair bit of power that can be unlocked when combined with Google Sheets as a data store.

Any/all suggestions, ideas or bugs - please do raise an [issue](https://github.com/GrandyB/SheeTXT/issues).

## Config

Configs can be placed anywhere, selected through the file chooser in the UI.

```json
{
	"projectName": "myProject",
	"worksheetName": "Sheet1",
	"apiKey": "get_your_key_from_https://console.developers.google.com/",
	"spreadsheetId": "12YrqfVJENT6FJZB5NZrv2fHKg36XEy2jTE5X-mwC61g",
	"cells": [
		{ "cell": "B1", "file": "b1.txt" },
		{ "cell": "B2", "file": "b2.txt" },
		{ "cell": "B3", "file": "b3.txt" }
	]
}
```

- `"projectName"` - becomes the folder name; files are generated within `/files/projectName`
- `"worksheetName"` - the sheet within the spreadsheet to observe
- `"apiKey"` - from Google Developer Console, more detail below
- `"cells"` - the configuration of cells to files
