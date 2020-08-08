# SheeTXT

The aim of the project is to create a simple way to use Google Spreadsheet data as text files within OBS.

Microsoft Excel (and other local sheet programs) often have the capability to record macros and save to files, however this requires some technical knowledge to do so, and requires the right type of sheet program  (as each have their own language for writing macros, sometimes making it difficult to share).

There's also a project or two such as [https://github.com/rewindrl/updater](RewindRL)'s updater system, which goes from Google Sheets into browser-based graphics.

SheeTXT is (possibly) the first to go straight to text files.

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
