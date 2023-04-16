// Fill out your copyright notice in the Description page of Project Settings.

#pragma once

#include "CoreMinimal.h"
#include "Engine/TriggerBox.h"
#include "GameFramework/Actor.h"
#include "BifrostCharacter.h"
#include "DimensionalPin.h"
#include "EngineUtils.h"
#include "Gameframework/SpringArmComponent.h"
#include "Bridge.generated.h"

/**
 * This class represents the functionality of a bridge to another dimension.
 * 
 * Currently it only works for 2 set dimensions
 */
UCLASS()
class BIFROST_API ABridge : public ATriggerBox
{
	GENERATED_BODY()

protected:
	virtual void BeginPlay() override;

public:
	ABridge();

	//Function to teleport player to other dimension
	UFUNCTION()
		void EnterTeleporter(class AActor* overlappedActor, class AActor* otherActor);

	//Variable used in dimension calculation
	UPROPERTY(BlueprintReadWrite, VisibleAnywhere)
		int curDim;

	UPROPERTY(BlueprintReadWrite, VisibleAnywhere)
		bool canUse;

	UPROPERTY(BlueprintReadWrite, VisibleAnywhere)
		int flipped;

	UPROPERTY(BlueprintReadWrite, VisibleAnywhere)
		FVector vecToGoTo;

	UPROPERTY(BlueprintReadWrite, VisibleAnywhere)
		FVector PinDim1;

	UPROPERTY(BlueprintReadWrite, VisibleAnywhere)
		FVector PinDim2;

	UPROPERTY(BlueprintReadWrite, VisibleAnywhere)
		FVector OtherSideLoc;

	UPROPERTY(BlueprintReadWrite, VisibleAnywhere)
		FVector BridgeLoc;

	UPROPERTY(BlueprintReadWrite, VisibleAnywhere)
		ABifrostCharacter* character1;
	
};
