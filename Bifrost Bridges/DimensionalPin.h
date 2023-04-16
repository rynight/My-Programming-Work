// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "GameFramework/Actor.h"
#include "DimensionalPin.generated.h"

UCLASS()
class BIFROST_API ADimensionalPin : public AActor
{
	GENERATED_BODY()
	
public:	
	// Sets default values for this actor's properties
	ADimensionalPin();

protected:
	// Called when the game starts or when spawned
	virtual void BeginPlay() override;

public:	
	
	UPROPERTY(EditAnywhere, meta = (AllowPrivateAccess = "true"))
		TSubclassOf<AActor> ActorToSpawn;

	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Dimension")
		int Dim;

	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Dimension")
		int Region;

	UPROPERTY(EditAnywhere, BlueprintReadOnly, Category = "Dimension")
		bool flip;

};
