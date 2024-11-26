package service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static constant.GameConstants.BOARD_SIZE;

public class MapService {
    public char[][] readMap(int stageNumber) throws IOException {
        String filePath = "stage/stage" + stageNumber + ".txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            List<String> lines = new ArrayList<>();
            String line;

            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }

            if (lines.isEmpty()) {
                throw new IllegalArgumentException("맵 파일이 비어있습니다.");
            }

            int height = lines.size();
            int width = lines.get(0).length();

            if (height != BOARD_SIZE || width != BOARD_SIZE) {
                throw new IllegalArgumentException(
                        String.format("맵 크기가 유효하지 않습니다. 필요한 크기: %dx%d, 실제 크기: %dx%d",
                                BOARD_SIZE, BOARD_SIZE, width, height)
                );
            }

            char[][] mapData = new char[BOARD_SIZE][BOARD_SIZE];
            for (int i = 0; i < height; i++) {
                String currentLine = lines.get(i);
                if (currentLine.length() != width) {
                    throw new IllegalArgumentException(
                            String.format("맵의 %d번째 행의 길이가 올바르지 않습니다. (예상: %d, 실제: %d)",
                                    i + 1, width, currentLine.length())
                    );
                }
                mapData[i] = currentLine.toCharArray();
            }

            return mapData;
        }
    }
}