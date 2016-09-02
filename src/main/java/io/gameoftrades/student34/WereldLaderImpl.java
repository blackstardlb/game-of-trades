package io.gameoftrades.student34;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.kaart.*;
import io.gameoftrades.model.lader.WereldLader;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.HandelType;
import io.gameoftrades.model.markt.Handelswaar;
import io.gameoftrades.model.markt.Markt;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WereldLaderImpl implements WereldLader {

    @Override
    public Wereld laad(String resource) {
        //
        // Gebruik this.getClass().getResourceAsStream(resource) om een resource van het classpath te lezen.
        //
        // Kijk in src/test/resources voor voorbeeld kaarten.
        //
        // TODO Laad de wereld!
        //
        try {
            return new MapParser(readWorldFile(resource)).getWereld();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private ArrayList<String> readWorldFile(String name) throws IOException {
        InputStream is = this.getClass().getResourceAsStream(name);
        if (is != null) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            ArrayList<String> file = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                file.add(line);
            }
            return file;
        }
        return null;
    }

    private class MapParser {
        private ArrayList<String> cityStrings;
        private ArrayList<String> marketStrings;
        private ArrayList<String> mapStrings;

        MapParser(ArrayList<String> file) {
            this.mapStrings = parseMapStrings(file);
            this.cityStrings = parseCityStrings(file);
            this.marketStrings = parseMarketStrings(file);
        }

        Wereld getWereld() {
            Kaart kaart = getKaart();
            List<Stad> steden = getSteden();
            Markt markt = getMarkt(steden);
            return new Wereld(kaart, steden, markt);
        }

        private ArrayList<String> parseMapStrings(ArrayList<String> file) {
            if (file.get(0).matches("\\d*,\\d*")) { // check if formatting matches: number, number
                String[] dimensions = file.get(0).split(",");
                int mapHeight = Integer.parseInt(dimensions[1]);
                ArrayList<String> mapStrings = new ArrayList<>();
                for (int i = 1; i <= mapHeight; i++) {
                    mapStrings.add(file.get(i));
                }
                return mapStrings;
            }
            return null;
        }

        private ArrayList<String> parseCityStrings(ArrayList<String> file) {
            int mapHeight = mapStrings.size();
            if (isStringInt(file.get(mapHeight + 1))) {
                int ammountOfCities = Integer.parseInt(file.get(mapHeight + 1));
                ArrayList<String> cityStrings = new ArrayList<>();
                for (int i = mapHeight + 2; i < mapHeight + 2 + ammountOfCities; i++) {
                    cityStrings.add(file.get(i));
                }
                return cityStrings;
            }
            return null;
        }

        private ArrayList<String> parseMarketStrings(ArrayList<String> file) {
            int mapHeight = mapStrings.size();
            int ammountOfCities = cityStrings.size();
            if (isStringInt(file.get(mapHeight + 2 + ammountOfCities))) {
                int marktAmmount = Integer.parseInt(file.get(mapHeight + 2 + ammountOfCities));
                ArrayList<String> marketStrings = new ArrayList<>();
                for (int i = mapHeight + 3 + ammountOfCities; i < mapHeight + 3 + ammountOfCities + marktAmmount; i++) {
                    marketStrings.add(file.get(i));
                }
                return marketStrings;
            }
            return null;
        }

        private Kaart getKaart() {
            Kaart kaart = new Kaart(mapStrings.get(0).length(), mapStrings.size());
            for (int x = 0; x < mapStrings.size(); x++) {
                for (int y = 0; y < mapStrings.get(x).length(); y++) {
                    new Terrein(kaart, Coordinaat.op(x, y), new TerreinTypeParser().parse(mapStrings.get(x).charAt(y)));
                }
            }
            return kaart;
        }

        private List<Stad> getSteden() {
            List<Stad> steden = new ArrayList<>();
            for (String string : cityStrings) {
                steden.add(getStad(string));
            }
            return steden;
        }

        private Stad getStad(String string) {
            String[] parts = string.split(",");
            if (parts.length == 3 && isStringInt(parts[0]) && isStringInt(parts[1])) {
                return new Stad(Coordinaat.op(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])), parts[2]);
            }
            return null;
        }

        private Markt getMarkt(List<Stad> steden) {
            List<Handel> handels = new ArrayList<>();
            for (String string : marketStrings) {
                handels.add(getHandel(string, steden));
            }
            return new Markt(handels);
        }

        private Handel getHandel(String string, List<Stad> steden) {
            String[] parts = string.split(",");
            if (parts.length == 4 && isStringInt(parts[3])) {
                for (Stad stad : steden) {
                    if (parts[0].equals(stad.getNaam())) {
                        HandelType handelType = new HandelTypeParser().parse(parts[1]);
                        return new Handel(stad, handelType, new Handelswaar(parts[2]), Integer.parseInt(parts[3]));
                    }
                }
            }
            return null;
        }

        private boolean isStringInt(String string) {
            return string.matches("\\d+");
        }
    }

    private class HandelTypeParser {
        HandelType parse(String handelTypeString) {
            for (HandelType handelType : HandelType.values()) {
                if (handelType.toString().equals(handelTypeString)) {
                    return handelType;
                }
            }
            throw new IllegalArgumentException("HandelType " + handelTypeString + " bestaat niet");
        }
    }

    private class TerreinTypeParser {
        TerreinType parse(char terreinChar) {
            for (TerreinType terreinType : TerreinType.values()) {
                if (terreinType.getLetter() == terreinChar) {
                    return terreinType;
                }
            }
            throw new IllegalArgumentException("TerreinType met char " + terreinChar + " bestaat niet");
        }
    }
}