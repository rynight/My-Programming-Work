// Fill out your copyright notice in the Description page of Project Settings.


#include "DimensionalPin.h"

// Sets default values
ADimensionalPin::ADimensionalPin()
{
 	// Set this actor to call Tick() every frame.  You can turn this off to improve performance if you don't need it.
	PrimaryActorTick.bCanEverTick = true;

}

// Called when the game starts or when spawned
void ADimensionalPin::BeginPlay()
{
	Super::BeginPlay();
	
	const FVector Location = GetActorLocation();
	const FRotator Rotation = GetActorRotation();

	GetWorld()->SpawnActor<AActor>(Location, Rotation);
}

