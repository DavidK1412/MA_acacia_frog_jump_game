package policy

import (
	"encoding/json"
	"fmt"
	"os"
	"sync"
)

type PolicyLoader struct {
	tables     map[int]*PolicyTable
	loadSource string
	mu         sync.RWMutex
}

func NewPolicyLoader(useFilesystem bool) *PolicyLoader {
	loader := &PolicyLoader{
		tables: make(map[int]*PolicyTable),
	}
	
	if useFilesystem {
		loader.loadSource = "FILESYSTEM"
		loader.loadFromFilesystem()
	} else {
		loader.loadSource = "EMBEDDED"
	}
	
	return loader
}

func (pl *PolicyLoader) loadFromFilesystem() {
	levels := []int{3, 4, 5}
	
	for _, level := range levels {
		filename := fmt.Sprintf("graphs/level_%d.json", level)
		data, err := os.ReadFile(filename)
		if err != nil {
			continue
		}
		
		var table PolicyTable
		if err := json.Unmarshal(data, &table); err != nil {
			continue
		}
		
		pl.mu.Lock()
		pl.tables[level] = &table
		pl.mu.Unlock()
	}
}

func (pl *PolicyLoader) GetPolicyTable(level int) (*PolicyTable, bool) {
	pl.mu.RLock()
	defer pl.mu.RUnlock()
	
	table, exists := pl.tables[level]
	return table, exists
}

func (pl *PolicyLoader) LoadSource() string {
	return pl.loadSource
}
