import pygame


def run_start_menu():
    pygame.init()
    width, height = 640, 480
    screen = pygame.display.set_mode((width, height))
    pygame.display.set_caption("Medieval Duel - Start Menu")

    font = pygame.font.Font(None, 48)
    small_font = pygame.font.Font(None, 36)
    clock = pygame.time.Clock()

    running = True
    start_game = False
    while running:
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    running = False
                else:
                    start_game = True
                    running = False

        screen.fill((0, 0, 0))
        title_surf = font.render("Medieval Duel", True, (255, 255, 255))
        prompt_surf = small_font.render("Press any key to start", True, (200, 200, 200))
        screen.blit(title_surf, ((width - title_surf.get_width()) // 2, height // 3))
        screen.blit(prompt_surf, ((width - prompt_surf.get_width()) // 2, height // 2))
        pygame.display.flip()
        clock.tick(60)

    pygame.quit()
    return start_game


if __name__ == "__main__":
    if run_start_menu():
        print("Game starting...")
    else:
        print("Quit from start menu")
