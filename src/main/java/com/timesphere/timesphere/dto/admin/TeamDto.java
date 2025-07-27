package com.timesphere.timesphere.dto.admin;

import com.timesphere.timesphere.dto.member.TeamMemberDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
    private String teamId;
    private String teamName;
    private String description;
    private String createdBy;
    private List<TeamMemberDTO> members;

    private int totalFiles;
    private int totalComments;
    private int totalTasks;
}
