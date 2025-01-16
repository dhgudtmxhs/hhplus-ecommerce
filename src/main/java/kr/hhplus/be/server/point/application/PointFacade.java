package kr.hhplus.be.server.point.application;

import kr.hhplus.be.server.point.domain.Point;
import kr.hhplus.be.server.point.domain.PointService;
import kr.hhplus.be.server.user.domain.User;
import kr.hhplus.be.server.user.domain.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointFacade {

    private final UserService userService;
    private final PointService pointService;
    private final PointInfoMapper pointInfoMapper;

    public PointInfo getPoint(PointCommand command) {
        userService.getUser(command.userId());
        Point point = pointService.getPoint(command.userId());

        return pointInfoMapper.toPointInfo(point);
    }

    public ChargePointInfo chargePoint(ChargePointCommand command) {
        userService.getUser(command.userId());
        Point updatedPoint = pointService.chargePoint(command.userId(), command.amount());

        return pointInfoMapper.toChargePointInfo(updatedPoint);
    }
}
