package com.timesphere.timesphere.controller;

import com.timesphere.timesphere.dto.kanban.KanbanBoardResponseDTO;
import com.timesphere.timesphere.service.KanbanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kanban")
@CrossOrigin(origins = "http://localhost:5173")
public class KanbanController {

    private final KanbanService kanbanService;

    @GetMapping("/{workspaceId}/kanban-board")
    public ResponseEntity<KanbanBoardResponseDTO> getBoard(@PathVariable String workspaceId) {
        return ResponseEntity.ok(kanbanService.getKanbanBoard(workspaceId));
    }
}