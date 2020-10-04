CREATE TABLE `State` (
  `ID` INTEGER PRIMARY KEY,
  `Name` text,
  `Color` text
);

CREATE TABLE `Library` (
  `ID` text PRIMARY KEY,
  `Name` text
);

CREATE TABLE `Series` (
  `ID` text PRIMARY KEY,
  `Name` text,
  `Infotext` text,
  `Image` text,
  `Library` text,
  FOREIGN KEY (`Library`) REFERENCES `Library` (`ID`)
);

CREATE TABLE `Exemplar` (
  `ID` text PRIMARY KEY,
  `Name` text,
  `Infotext` text,
  `Image` text,
  `Series` text,
  `Library` text,
  `State` INTEGER,
  FOREIGN KEY (`Series`) REFERENCES `Series` (`ID`),
  FOREIGN KEY (`Library`) REFERENCES `Library` (`ID`),
  FOREIGN KEY (`State`) REFERENCES `State` (`ID`)
);

CREATE TABLE `ExemplarAdditionalInfo` (
  `ID` text PRIMARY KEY,
  `Exemplar` text,
  `Typ` text,
  `Text` text,
  FOREIGN KEY (`Exemplar`) REFERENCES `Exemplar` (`ID`)
);

CREATE TABLE `SeriesAdditionalInfo` (
  `ID` text PRIMARY KEY,
  `Series` text,
  `Typ` text,
  `Text` text,
  FOREIGN KEY (`Series`) REFERENCES `Series` (`ID`)
);

CREATE TABLE `Relation` (
  `ID` INTEGER PRIMARY KEY,
  `Name` text
);

CREATE TABLE `RelationSeries` (
  `Father` text,
  `Child` bloc,
  `Relation` INTEGER,
  PRIMARY KEY (`Father`, `Child`),
  FOREIGN KEY (`Father`) REFERENCES `Series` (`ID`),
  FOREIGN KEY (`Child`) REFERENCES `Series` (`ID`),
  FOREIGN KEY (`Relation`) REFERENCES `Relation` (`ID`)
);

CREATE TABLE `RelationExemplar` (
  `Father` text,
  `Child` text,
  `Relation` INTEGER,
  PRIMARY KEY (`Father`, `Child`),
  FOREIGN KEY (`Father`) REFERENCES `Exemplar` (`ID`),
  FOREIGN KEY (`Child`) REFERENCES `Exemplar` (`ID`),
  FOREIGN KEY (`Relation`) REFERENCES `Relation` (`ID`)
);

REPLACE INTO State (id, name, color)
VALUES(1, "Angekündigt", "#9c9c9c"),
	(2, "Erhältlich", "#c40000"),
	(3, "In Besitz", "#18b300"),
	(4, "Vorbestellt", "#00abb8"),
	(5, "Nicht Verfügbar", "#000000");

REPLACE INTO Relation (id, name)
VALUES(-1, "Nachfolger"),
	(-2, "Spinoff");