package com.plcpipeline.ingestion.controllers;

import com.plcpipeline.ingestion.dtos.TerminalDto;
import com.plcpipeline.ingestion.services.TerminalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/terminals")
public class TerminalController {
    private final TerminalService terminalService;

    public TerminalController(TerminalService terminalService) {
        this.terminalService = terminalService;
    }

    @GetMapping
    public ResponseEntity<List<TerminalDto>> getAllTerminals() {
        return ResponseEntity.ok(terminalService.getAllTerminals());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TerminalDto> getTerminalById(@PathVariable Long id) {
        return ResponseEntity.ok(terminalService.getTerminalById(id));
    }

    @PostMapping
    public ResponseEntity<TerminalDto> createTerminal(@RequestBody TerminalDto dto) {
        TerminalDto created = terminalService.createTerminal(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TerminalDto> updateTerminal(@PathVariable Long id, @RequestBody TerminalDto dto) {
        return ResponseEntity.ok(terminalService.updateTerminal(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTerminal(@PathVariable Long id) {
        terminalService.deleteTerminal(id);
        return ResponseEntity.noContent().build();
    }
}
