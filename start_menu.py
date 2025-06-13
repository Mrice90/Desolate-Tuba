import pygame


def run_start_menu():
    """Display a simple flashing start screen.

    Returns True if the user chooses to start the game, False otherwise.
    """
    pygame.init()
    width, height = 640, 480
    screen = pygame.display.set_mode((width, height))
    pygame.display.set_caption("Medieval Duel - Start Menu")

    title_font = pygame.font.Font(None, 64)
    start_font = pygame.font.Font(None, 48)

    clock = pygame.time.Clock()
    flash_visible = True
    flash_timer = 0

    # Pre-render text surfaces
    title_surf = title_font.render("Medieval Duel", True, (255, 255, 255))
    start_surf = start_font.render("START", True, (255, 255, 255))
    start_rect = start_surf.get_rect(center=(width // 2, height // 2))

    running = True
    start_game = False
    while running:
        dt = clock.tick(60)
        for event in pygame.event.get():
            if event.type == pygame.QUIT:
                running = False
            elif event.type == pygame.KEYDOWN:
                if event.key == pygame.K_ESCAPE:
                    running = False
                else:
                    start_game = True
                    running = False
            elif event.type == pygame.MOUSEBUTTONDOWN and event.button == 1:
                if start_rect.collidepoint(event.pos):
                    start_game = True
                    running = False

        flash_timer += dt
        if flash_timer >= 500:  # toggle twice per second
            flash_visible = not flash_visible
            flash_timer = 0

        screen.fill((0, 0, 0))
        screen.blit(title_surf, ((width - title_surf.get_width()) // 2, height // 3))
        if flash_visible:
            screen.blit(start_surf, start_rect)
        pygame.display.flip()

    pygame.quit()
    return start_game


if __name__ == "__main__":
    if run_start_menu():
        print("Game starting...")
    else:
        print("Quit from start menu")
