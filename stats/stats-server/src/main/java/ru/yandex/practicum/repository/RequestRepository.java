package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.RequestDto;
import ru.yandex.practicum.RequestForStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestRepository extends JpaRepository<RequestDto, Integer> {
    @Query("select new ru.yandex.practicum.RequestForStatDto(rd.app, rd.uri, count(rd.ip)) " +
            "from RequestDto as rd "+
            "where rd.timestamp between ?1 and ?2 "+
            "and rd.uri in (?3) "+
            "group by rd.ip, rd.app, rd.uri "+
            "order by count(rd.ip) desc")
    List<RequestForStatDto> getStat(LocalDateTime start,
                                    LocalDateTime end,
                                    String[] uris);

    @Query("select new ru.yandex.practicum.RequestForStatDto(rd.app, rd.uri, count(distinct rd.ip)) " +
            "from RequestDto as rd "+
            "where rd.timestamp between ?1 and ?2 "+
            "and rd.uri in (?3) "+
            "group by rd.ip, rd.app, rd.uri "+
            "order by count(distinct rd.ip) desc")
    List<RequestForStatDto> getStatUniqueIp(LocalDateTime start,
                                            LocalDateTime end,
                                            String[] uris);

    @Query("select new ru.yandex.practicum.RequestForStatDto(rd.app, rd.uri, count(rd.ip)) " +
            "from RequestDto as rd "+
            "where rd.timestamp between ?1 and ?2 "+
            "group by rd.ip, rd.app, rd.uri "+
            "order by count(rd.ip) desc")
    List<RequestForStatDto> getStatWithoutUris(LocalDateTime start,
                                    LocalDateTime end);

    @Query("select new ru.yandex.practicum.RequestForStatDto(rd.app, rd.uri, count(distinct rd.ip)) " +
            "from RequestDto as rd "+
            "where rd.timestamp between ?1 and ?2 "+
            "group by rd.ip, rd.app, rd.uri "+
            "order by count(distinct rd.ip) desc")
    List<RequestForStatDto> getStatUniqueIpWithoutUris(LocalDateTime start,
                                            LocalDateTime end);
}
