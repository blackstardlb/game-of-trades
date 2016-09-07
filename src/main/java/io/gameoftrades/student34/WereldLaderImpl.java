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
import java.util.function.Consumer;

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
        return new WorldParser(resource).getWereld();
    }

    private class WorldParser {
        private Kaart kaart;
        private List<Stad> cities;
        private Markt market;

        WorldParser(String resourceName) {
            ArrayList<String> file = readWorldFile(resourceName);
            kaart = parseMapStrings(file).getKaart();
            cities = parseCityStrings(file).getCities();
            market = parseMarketStrings(file).getMarket();
        }

        Wereld getWereld() {
            return new Wereld(kaart, cities, market);
        }

        private ArrayList<String> readWorldFile(String name) {
            InputStream is = this.getClass().getResourceAsStream(name);
            try {
                if (is != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String line;
                    ArrayList<String> file = new ArrayList<>();
                    while ((line = br.readLine()) != null) {
                        file.add(line.replaceAll("\\s+$", ""));
                    }
                    return file;
                } else {
                    throw new IllegalArgumentException("File not found " + name);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        private MapParser parseMapStrings(ArrayList<String> file) {
            if (file.get(0).matches("\\d*,\\d*")) { // check if formatting matches: number,number
                String[] dimensions = file.get(0).split(",");
                int mapHeight = Integer.parseInt(dimensions[1]);
                int mapWidth = Integer.parseInt(dimensions[0]);
                ArrayList<String> mapStrings = new ArrayList<>();
                if (file.size() >= mapHeight) {
                    for (int i = 1; i <= mapHeight; i++) {
                        mapStrings.add(file.get(i));
                    }
                    return new MapParser(mapWidth, mapHeight, mapStrings);
                }
            }
            throw new IllegalArgumentException("Map formatting error");
        }

        private CityParser parseCityStrings(ArrayList<String> file) {
            if (file.size() >= kaart.getHoogte() + 1) {
                ArrayList<String> subArray = new ArrayList<>();

                for (int i = kaart.getHoogte() + 1; i < file.size(); i++) {
                    subArray.add(file.get(i));
                }

                if (isStringInt(subArray.get(0))) {
                    int ammountOfCities = Integer.parseInt(subArray.get(0));
                    subArray.remove(0);
                    ArrayList<String> cityStrings = new ArrayList<>();
                    if (subArray.size() >= ammountOfCities) {
                        for (int i = 0; i < ammountOfCities; i++) {
                            cityStrings.add(subArray.get(i));
                        }
                        return new CityParser(cityStrings);
                    }
                }
            }
            throw new IllegalArgumentException("City format error");
        }

        private MarketParser parseMarketStrings(ArrayList<String> file) {
            if (file.size() >= kaart.getHoogte() + cities.size() + 3) {
                ArrayList<String> subArray = new ArrayList<>();

                for (int i = kaart.getHoogte() + cities.size() + 2; i < file.size(); i++) {
                    subArray.add(file.get(i));
                }

                if (isStringInt(subArray.get(0))) {
                    int ammountOfMarkets = Integer.parseInt(subArray.get(0));
                    subArray.remove(0);
                    ArrayList<String> marketStrings = new ArrayList<>();
                    if (subArray.size() >= ammountOfMarkets) {
                        for (int i = 0; i < ammountOfMarkets; i++) {
                            marketStrings.add(subArray.get(i));
                        }
                        return new MarketParser(marketStrings, cities);
                    }
                }
            }
            throw new IllegalArgumentException("Market format error");
        }

        private boolean isStringInt(String string) {
            return string.matches("\\d+");
        }

        private class MapParser {
            private Kaart kaart;

            MapParser(int width, int height, ArrayList<String> mapTerrein) {
                kaart = new Kaart(width, height);
                for (String terreinString : mapTerrein) {
                    checkLineMatchMapFormatting(terreinString, kaart.getBreedte());
                }
                this.addTerrein(mapTerrein);
            }

            private void addTerrein(ArrayList<String> mapTerrein) {
                for (int y = 0; y < kaart.getHoogte(); y++) {
                    for (int x = 0; x < kaart.getBreedte(); x++) {
                        new Terrein(kaart, Coordinaat.op(x, y), new TerreinTypeParser().parse(mapTerrein.get(y).charAt(x)));
                    }
                }
            }

            private void checkLineMatchMapFormatting(String line, int mapWidth) {
                String regex = "[" + new TerreinTypeParser().getLetters() + "]{" + mapWidth + "}";
                if (!line.matches(regex)) {
                    throw new IllegalArgumentException("Line format error: " + line);
                }
            }

            public Kaart getKaart() {
                return kaart;
            }
        }

        private class CityParser {
            private List<Stad> cities;

            CityParser(ArrayList<String> cityStrings) {
                cities = new ArrayList<>();
                for (String cityString : cityStrings) {
                    cities.add(parseStad(cityString));
                }
            }

            private Stad parseStad(String string) {
                String[] parts = string.split(",");
                if (parts.length == 3 && isStringInt(parts[0]) && isStringInt(parts[1])) {
                    return new Stad(Coordinaat.op(Integer.parseInt(parts[0]), Integer.parseInt(parts[1])), parts[2]);
                }
                throw new IllegalArgumentException("City format error" + string);
            }

            List<Stad> getCities() {
                return cities;
            }
        }

        private class MarketParser {
            private Markt market;

            MarketParser(ArrayList<String> marketStrings, List<Stad> cities) {
                List<Handel> handels = new ArrayList<>();
                for (String string : marketStrings) {
                    handels.add(getHandel(string, cities));
                }
                market = new Markt(handels);
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
                throw new IllegalArgumentException("Handel formatting error: " + string);
            }

            public Markt getMarket() {
                return market;
            }
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

        String getLetters() {
            StringBuilder sb = new StringBuilder();
            for (TerreinType terreinType : TerreinType.values()) {
                sb.append(terreinType.getLetter());
            }
            return sb.toString();
        }
    }
}