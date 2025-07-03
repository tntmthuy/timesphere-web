package com.timesphere.timesphere.service;

import com.timesphere.timesphere.dto.kanban.KanbanBoardResponseDTO;
import com.timesphere.timesphere.entity.TeamWorkspace;
import com.timesphere.timesphere.exception.AppException;
import com.timesphere.timesphere.exception.ErrorCode;
import com.timesphere.timesphere.mapper.KanbanBoardMapper;
import com.timesphere.timesphere.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KanbanService {

    private final TeamRepository workspaceRepo;

    public KanbanBoardResponseDTO getKanbanBoard(String workspaceId) {
        TeamWorkspace workspace = workspaceRepo.findWithBoardById(workspaceId)
                .orElseThrow(() -> new AppException(ErrorCode.TEAM_NOT_FOUND));

        return KanbanBoardMapper.toDto(workspace);
    }
}
