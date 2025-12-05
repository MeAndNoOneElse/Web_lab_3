package service;

import dto.ResultDTO;
import dto.PointDTO;

import entity.ResultEntity;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import repository.RepositoryInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@PlainService
@ApplicationScoped
public class AreaCheckService implements AreaCheckServiceInterface, Serializable {

    private static final Logger LOGGER = Logger.getLogger(AreaCheckService.class.getName());

    @Inject
    private RepositoryInterface repository;

    @Override
    public ResultDTO checkAndSave(PointDTO point) {
        long startTime = System.nanoTime();
        boolean hit = checkHit(point.getX(), point.getY(), point.getR());
        long endTime = System.nanoTime();
        long executionTimeNano = endTime - startTime;
        // Конвертируем в миллисекунды и работаем только с миллисекундами везде дальше
        long executionTimeMillis = executionTimeNano/1000;
        Date date = new Date();

        ResultDTO resultDTO = new ResultDTO(point.getX(), point.getY(), point.getR(), hit, date, executionTimeMillis);

        // Передавать в entity тоже миллисекунды — убедитесь, что поле в сущности long и геттер называется getExecutionTimeMillis()
        ResultEntity result = new ResultEntity(1, point.getX(), Double.parseDouble(point.getY()), point.getR(), hit, date, executionTimeMillis);
        // логирование обращения к БД при сохранении (в миллисекундах)
        LOGGER.info(() -> String.format("[DB] saveResult() called — saving new result: x=%.4f, y=%s, r=%.4f, hit=%b, execMs=%d",
                point.getX(), point.getY(), point.getR(), hit, executionTimeMillis));
        repository.saveResult(result);
        LOGGER.fine("[DB] saveResult() completed");

        return resultDTO;
    }

    @Override
    public List<ResultDTO> getAllResults() {
        // Защита: если repository не инжектирован — логируем и возвращаем пустой список
        if (repository == null) {
            LOGGER.warning("[DB] repository is null in getAllResults() — returning empty list to avoid NPE during init");
            return new ArrayList<>();
        }

        LOGGER.info("[DB] getAllPoints() called — fetching from database at " + new Date());

        List<ResultEntity> entities = repository.getAllPoints();
        int rows = (entities == null) ? 0 : entities.size();
        LOGGER.info(() -> String.format("[DB] Loaded %d rows from database (source=DB)", rows));

        List<ResultDTO> results = new ArrayList<>();
        for (ResultEntity entity : entities) {
            long execMs = extractExecutionTimeMillis(entity);
            ResultDTO dto = new ResultDTO(
                entity.getX(),
                String.valueOf(entity.getY()),
                entity.getR(),
                entity.isHit(),
                entity.getTimestamp(),
                /* executionTimeMs: */ execMs
            );
            results.add(dto);
        }
        LOGGER.fine(() -> String.format("[DB] getAllPoints() returned %d rows", results.size()));
        return results;
    }

    @Override
    public void deleteAllResults() {
        LOGGER.info("[DB] deleteAllPoints() called — deleting all results from DB");
        repository.deleteAllPoints();
        LOGGER.fine("[DB] deleteAllPoints() completed");
    }

    private boolean checkHit(double x, String yStr, double r) {
        double y;
        try {
            y = Double.parseDouble(yStr);
        } catch (Exception e) {
            return false;
        }
        if (x >= 0 && y >= 0) {
            return x <= r && y <= r / 2;
        }
        if (x <= 0 && y >= 0) {
            return y <= x + r / 2;
        }
        if (x >= 0 && y <= 0) {
            return (x * x + y * y) <= (r * r);
        }
        return false;
    }

    /**
     * Попытаться получить execution time из сущности и вернуть значение в миллисекундах.
     * Поддерживает геттеры: getExecutionTimeMillis, getExecutionTimeMs, getExecutionTimeNano,
     * getExecutionTime, getExecutionTimeNanos и др. Если найдено nano-значение — конвертируем в ms.
     */
    private long extractExecutionTimeMillis(ResultEntity entity) {
        if (entity == null) return 0L;
        String[] candidates = new String[]{
            "getExecutionTimeMillis", "getExecutionTimeMs", "getExecutionTimeMillisec",
            "getExecutionTime", "getExecutionTimeNano", "getExecutionTimeNanos", "getExecutionTimeNanoseconds"
        };
        for (String name : candidates) {
            try {
                java.lang.reflect.Method m = entity.getClass().getMethod(name);
                Object val = m.invoke(entity);
                if (val == null) continue;
                if (val instanceof Number) {
                    long raw = ((Number) val).longValue();
                    if (name.toLowerCase().contains("nano")) {
                        long ms = raw / 1_000L;
                        LOGGER.fine(() -> String.format("[DB] extracted execution time from %s: %d ns -> %d ms", name, raw, ms));
                        return ms;
                    } else {
                        LOGGER.fine(() -> String.format("[DB] extracted execution time from %s: %d (assume ms)", name, raw));
                        return raw;
                    }
                }
                if (val instanceof String) {
                    final String sval = (String) val;
                    try {
                        long parsed = Long.parseLong(sval);
                        final long parsedMs = name.toLowerCase().contains("nano") ? (parsed / 1_000_000L) : parsed;
                        LOGGER.fine(() -> String.format("[DB] parsed execution time from %s: %s -> %d ms", name, sval, parsedMs));
                        return parsedMs;
                    } catch (NumberFormatException ignored) {
                        // не удалось распарсить, пробуем следующий кандидат
                    }
                }
            } catch (NoSuchMethodException ignored) {
                // метод не найден — пробуем следующий кандидат
            } catch (Exception e) {
                LOGGER.warning(() -> "[DB] error while extracting execution time via reflection: " + e);
                break;
            }
        }
        LOGGER.warning("[DB] unable to extract execution time from ResultEntity, returning 0 ms");
        return 0L;
    }
}