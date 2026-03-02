package main

import (
	"fmt"
	"graph-api/internal/infrastructure/policygen"
	"os"
)

func main() {
	fmt.Println("🔧 Generador de Policy Tables")
	fmt.Println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
	
	generator := policygen.NewPolicyGenerator()
	
	levels := []int{3, 4, 5}
	
	for _, level := range levels {
		fmt.Printf("\n📊 Generando policy table para N=%d...\n", level)
		
		policyTable, err := generator.Generate(level)
		if err != nil {
			fmt.Printf("❌ Error: %v\n", err)
			os.Exit(1)
		}
		
		filename := fmt.Sprintf("graphs/level_%d.json", level)
		err = generator.SaveToFile(policyTable, filename)
		if err != nil {
			fmt.Printf("❌ Error guardando: %v\n", err)
			os.Exit(1)
		}
		
		fmt.Printf("✅ Generada: %s\n", filename)
		fmt.Printf("   Estados: %d\n", len(policyTable.Dist))
		fmt.Printf("   Versión: %s\n", policyTable.Version)
	}
	
	fmt.Println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
	fmt.Println("🎉 Policy tables generadas exitosamente")
}
