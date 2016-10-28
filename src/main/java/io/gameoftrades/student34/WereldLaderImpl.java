package io.gameoftrades.student34;

import io.gameoftrades.model.Wereld;
import io.gameoftrades.model.kaart.*;
import io.gameoftrades.model.lader.WereldLader;
import io.gameoftrades.model.markt.Handel;
import io.gameoftrades.model.markt.HandelType;
import io.gameoftrades.model.markt.Handelswaar;
import io.gameoftrades.model.markt.Markt;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class WereldLaderImpl implements WereldLader {
    private List<Stad> cities;
    private Queue<String> file;

    @Override
    public Wereld laad(String resource) {
        file = readWorldFile(resource);
        Kaart kaart = new MapParser().getKaart();
        cities = new CityParser().getCities();
        Markt market = new MarketParser().getMarket();
        return new Wereld(kaart, cities, market);
    }

    private Queue<String> readWorldFile(String name) {
        InputStream is = this.getClass().getResourceAsStream(name);
        try {
            if (is != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                Queue<String> file = new LinkedList<>();
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

    private int parseIntFromQueue() throws Exception {
        if (!file.isEmpty()) {
            String queString = file.poll();
            if (isStringInt(queString)) {
                return Integer.parseInt(queString);
            }
        }
        throw new Exception("Queue is empty or string isn't an integer");
    }

    private boolean isStringInt(String string) {
        return string.matches("\\d+");
    }

    private class MapParser {
        private Kaart kaart;

        MapParser() {
            Dimension dimension = parseMapDimension();
            kaart = new Kaart((int) dimension.getWidth(), (int) dimension.getHeight());
            for (int i = 0; i < dimension.getHeight() && !file.isEmpty(); i++) {
                String line = file.poll();
                checkLineMatchMapFormatting(line, kaart.getBreedte());
                this.addTerrein(line, i);
            }
        }

        private Dimension parseMapDimension() {
            if (!file.isEmpty()) {
                String dimensionString = file.poll();
                if (dimensionString.matches("\\d*,\\d*")) {
                    String[] dimensions = dimensionString.split(",");
                    int mapHeight = Integer.parseInt(dimensions[1]);
                    int mapWidth = Integer.parseInt(dimensions[0]);
                    return new Dimension(mapWidth, mapHeight);
                }
            }
            throw new IllegalArgumentException("Map formatting error");
        }

        private void addTerrein(String line, int yCord) {
            for (int x = 0; x < kaart.getBreedte(); x++) {
                new Terrein(kaart, Coordinaat.op(x, yCord), this.parseTerreinType(line.charAt(x)));
            }
        }

        private void checkLineMatchMapFormatting(String line, int mapWidth) {
            if (!line.matches("[" + this.getTerreinTypeLetters() + "]{" + mapWidth + "}")) {
                throw new IllegalArgumentException("Line format error: " + line);
            }
        }

        private TerreinType parseTerreinType(char terreinChar) {
            for (TerreinType terreinType : TerreinType.values()) {
                if (terreinType.getLetter() == terreinChar) {
                    return terreinType;
                }
            }
            throw new IllegalArgumentException("TerreinType met char " + terreinChar + " bestaat niet");
        }

        private String getTerreinTypeLetters() {
            StringBuilder sb = new StringBuilder();
            for (TerreinType terreinType : TerreinType.values()) {
                sb.append(terreinType.getLetter());
            }
            return sb.toString();
        }

        public Kaart getKaart() {
            return kaart;
        }
    }

    private class CityParser {
        private List<Stad> cities;

        CityParser() {
            int ammountOfCities = parseAmmountOfCities();
            cities = new ArrayList<>();

            for (int i = 0; i < ammountOfCities && !file.isEmpty(); i++) {
                cities.add(parseStad(file.poll()));
            }
        }

        private int parseAmmountOfCities() {
            try {
                return parseIntFromQueue();
            } catch (Exception e) {
                throw new IllegalArgumentException("City format error");
            }
        }

        private Stad parseStad(String string) {
            String[] parts = string.split(",");
            if (parts.length == 3 && isStringInt(parts[0]) && isStringInt(parts[1])) {
                int x = Integer.parseInt(parts[0]) - 1;
                int y = Integer.parseInt(parts[1]) - 1;
                if (x >= 0 && y >= 0) {
                    return new Stad(Coordinaat.op(x, y), parts[2]);
                }
            }
            throw new IllegalArgumentException("City format error" + string);
        }

        List<Stad> getCities() {
            return cities;
        }
    }

    private class MarketParser {
        private Markt market;

        MarketParser() {
            int amountOfMarkets = parseAmmountOfMarkets();
            List<Handel> handels = new ArrayList<>();
            for (int i = 0; i < amountOfMarkets && !file.isEmpty(); i++) {
                handels.add(parseHandel(file.poll(), cities));
            }
            market = new Markt(handels);
        }

        private int parseAmmountOfMarkets() {
            try {
                return parseIntFromQueue();
            } catch (Exception e) {
                throw new IllegalArgumentException("Market format error");
            }
        }

        private Handel parseHandel(String string, List<Stad> steden) {
            String[] parts = string.split(",");
            if (parts.length == 4 && isStringInt(parts[3])) {
                for (Stad stad : steden) {
                    if (parts[0].equals(stad.getNaam())) {
                        HandelType handelType = this.parseHandelType(parts[1]);
                        return new Handel(stad, handelType, new Handelswaar(parts[2]), Integer.parseInt(parts[3]));
                    }
                }
            }
            throw new IllegalArgumentException("Handel formatting error: " + string);
        }

        private HandelType parseHandelType(String handelTypeString) {
            for (HandelType handelType : HandelType.values()) {
                if (handelType.toString().equals(handelTypeString)) {
                    return handelType;
                }
            }
            throw new IllegalArgumentException("HandelType " + handelTypeString + " bestaat niet");
        }

        Markt getMarket() {
            return market;
        }
    }
}