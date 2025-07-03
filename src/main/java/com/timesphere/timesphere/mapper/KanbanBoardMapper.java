package com.timesphere.timesphere.mapper;

import com.timesphere.timesphere.dto.kanban.KanbanBoardResponseDTO;
import com.timesphere.timesphere.dto.kanban.KanbanColumnDTO;
import com.timesphere.timesphere.entity.KanbanColumn;
import com.timesphere.timesphere.entity.TeamWorkspace;

import java.util.List;
import java.util.Optional;

public class KanbanBoardMapper {

    public static KanbanBoardResponseDTO toDto(TeamWorkspace workspace) {
        List<KanbanColumnDTO> columnDtos = Optional.ofNullable(workspace.getColumns())
                .orElse(List.of())
                .stream()
                .map(KanbanColumnMapper::toDto)
                .toList();

        return KanbanBoardResponseDTO.builder()
                .workspaceId(workspace.getId())
                .workspaceName(workspace.getTeamName())
                .columns(columnDtos)
                .build();
    }
}